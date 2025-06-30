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

        val handler = GitLineHandler(project, root, GitCommand.DIFF)

        handler.addParameters("-w", "--shortstat", "--cached", sourceBranch)

        val settings = PluginSettingsService.instance.state
        val patterns = settings.exclusionPatterns.lines().filter { it.isNotBlank() }

        if (patterns.isNotEmpty()) {
            handler.addParameters("--")
            patterns.forEach { pattern ->
                handler.addParameters(":(exclude)$pattern")
            }
        }

        val result = Git.getInstance().runCommand(handler)

        println("result.outputAsJoinedString ::: ${result.outputAsJoinedString}")
        return if (result.exitCode == 0) {
            DiffResult.Success(parseShortStatOutput(result.outputAsJoinedString))
        } else {
            DiffResult.CommandError(result.errorOutputAsJoinedString)
        }
    }

    private fun parseShortStatOutput(output: String): DiffStats {
        val filesRegex = "(\\d+)\\s+files? changed".toRegex()
        val insertionsRegex = "(\\d+)\\s+insertions?\\(\\+\\)".toRegex()
        val deletionsRegex = "(\\d+)\\s+deletions?\\(-\\)".toRegex()

        val filesChanged = filesRegex.find(output)?.groupValues?.get(1)?.toInt() ?: 0
        val insertions = insertionsRegex.find(output)?.groupValues?.get(1)?.toInt() ?: 0
        val deletions = deletionsRegex.find(output)?.groupValues?.get(1)?.toInt() ?: 0

        return DiffStats(filesChanged, insertions, deletions)
    }
}