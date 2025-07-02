package io.github.sangcomz.diffhawk

import com.intellij.openapi.project.Project
import git4idea.commands.Git
import git4idea.commands.GitCommand
import git4idea.commands.GitLineHandler
import git4idea.repo.GitRepositoryManager

object GitDiffCalculator {

    sealed interface DiffResult {
        data class Success(val stats: DiffStats) : DiffResult
        data object NotGitRepository : DiffResult
        data class CommandError(val errorMessage: String) : DiffResult
    }

    data class DiffStats(val filesChanged: Int, val insertions: Int, val deletions: Int)

    fun getDiffStats(project: Project, sourceBranch: String): DiffResult {
        val repository = GitRepositoryManager.getInstance(project).repositories.firstOrNull()
        if (repository == null) {
            return DiffResult.NotGitRepository
        }

        val root = repository.root
        val settings = PluginSettingsService.instance.state
        val patterns = settings.exclusionPatterns.lines().filter { it.isNotBlank() }

        return try {
            val committedStats = executeDiffCommand(project, root, patterns) { handler ->
                handler.addParameters("-w", "--shortstat", sourceBranch, "HEAD")
            }
            
            val stagedStats = executeDiffCommand(project, root, patterns) { handler ->
                handler.addParameters("-w", "--shortstat", "--cached")
            }
            
            val unstagedStats = executeDiffCommand(project, root, patterns) { handler ->
                handler.addParameters("-w", "--shortstat")
            }
            
            val totalStats = combineStats(committedStats, stagedStats, unstagedStats)
            
            DiffResult.Success(totalStats)
            
        } catch (e: Exception) {
            DiffResult.CommandError(e.message ?: "Unknown error occurred")
        }
    }

    private fun executeDiffCommand(
        project: Project, 
        root: com.intellij.openapi.vfs.VirtualFile, 
        patterns: List<String>,
        configureHandler: (GitLineHandler) -> Unit
    ): DiffStats {
        val handler = GitLineHandler(project, root, GitCommand.DIFF)
        configureHandler(handler)
        
        if (patterns.isNotEmpty()) {
            handler.addParameters("--")
            patterns.forEach { pattern ->
                handler.addParameters(":(exclude)$pattern")
            }
        }
        
        val result = Git.getInstance().runCommand(handler)
        return if (result.exitCode == 0) {
            parseShortStatOutput(result.outputAsJoinedString)
        } else {
            DiffStats(0, 0, 0)
        }
    }

    private fun combineStats(vararg stats: DiffStats): DiffStats {
        val totalInsertions = stats.sumOf { it.insertions }
        val totalDeletions = stats.sumOf { it.deletions }
        
        val maxFilesChanged = stats.maxOfOrNull { it.filesChanged } ?: 0
        
        return DiffStats(maxFilesChanged, totalInsertions, totalDeletions)
    }

    private fun parseShortStatOutput(output: String): DiffStats {
        if (output.isBlank()) {
            return DiffStats(0, 0, 0)
        }
        
        val filesRegex = "(\\d+)\\s+files? changed".toRegex()
        val insertionsRegex = "(\\d+)\\s+insertions?\\(\\+\\)".toRegex()
        val deletionsRegex = "(\\d+)\\s+deletions?\\(-\\)".toRegex()

        val filesChanged = filesRegex.find(output)?.groupValues?.get(1)?.toInt() ?: 0
        val insertions = insertionsRegex.find(output)?.groupValues?.get(1)?.toInt() ?: 0
        val deletions = deletionsRegex.find(output)?.groupValues?.get(1)?.toInt() ?: 0

        return DiffStats(filesChanged, insertions, deletions)
    }
}
