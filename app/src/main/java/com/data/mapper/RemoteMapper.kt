package com.data.mapper

import com.data.model.Article
import com.data.model.Source

object RemoteMapper {
    fun Article.toClean(): Article = this.copy(
        title = title?.takeIf { it.isNotBlank() },
        description = description?.takeIf { it.isNotBlank() }
    )
}
