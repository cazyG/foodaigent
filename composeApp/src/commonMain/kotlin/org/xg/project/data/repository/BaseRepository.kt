package org.xg.project.data.repository

import org.xg.project.data.remote.ApiConfig

abstract class BaseRepository {
    protected val baseUrl: String = ApiConfig.API_BASE_URL
}
