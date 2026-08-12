package com.prexlauncher.feature.download

import com.prexlauncher.feature.download.enums.Category
import com.prexlauncher.feature.download.enums.ModLoader
import com.prexlauncher.feature.download.enums.Sort

/**
 * 用于平台进行搜索时，提供筛选信息
 */
class Filters {
    var name: String = ""
    var mcVersion: String? = null
    var modloader: ModLoader? = null
    var sort: Sort = Sort.RELEVANT
    var category: Category = Category.ALL

    override fun toString(): String {
        return "Filters(name='$name', mcVersion=$mcVersion, modloader=$modloader, sort=$sort, category=$category)"
    }
}