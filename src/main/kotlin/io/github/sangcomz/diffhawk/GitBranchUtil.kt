package io.github.sangcomz.diffhawk

import com.intellij.openapi.project.Project
import git4idea.repo.GitRepositoryManager

object GitBranchUtil {

    fun getAllBranches(project: Project): List<String> {
        val repository = GitRepositoryManager.getInstance(project).repositories.firstOrNull()
            ?: return emptyList()

        val localBranches = repository.branches.localBranches.map { it.name }
        val remoteBranches = repository.branches.remoteBranches.map { it.name }

        return (localBranches + remoteBranches).distinct().sorted()
    }
}