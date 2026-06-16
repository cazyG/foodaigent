package org.xg.project.feature.manualrecipe.usecase

import org.xg.project.data.remote.UploadService
import org.xg.project.domain.Result

class UploadImageUseCase(
    private val uploadService: UploadService
) {
    suspend operator fun invoke(fileName: String, imageBytes: ByteArray): Result<UploadService.UploadResult> {
        return uploadService.uploadImage(fileName, imageBytes)
    }
}
