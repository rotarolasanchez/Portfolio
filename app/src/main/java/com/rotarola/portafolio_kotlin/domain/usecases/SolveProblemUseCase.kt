package com.rotarola.portafolio_kotlin.domain.usecases

import com.rotarola.portafolio_kotlin.domain.repositories.ChatRepository
import javax.inject.Inject

class SolveProblemUseCase @Inject constructor(
    private val chatRepository: ChatRepository // Inyecta INTERFAZ
) {
    suspend operator fun invoke(problem: String): String {
        return chatRepository.solveProblem(problem)
    }
}