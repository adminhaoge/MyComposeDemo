package com.example.proxy.provider

import com.example.proxy.models.ActionResult
import com.example.proxy.models.GroupMemberProfile
import com.example.proxy.models.GroupProfile
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.flow.Flow

interface IGroupProvider {
    val joinedGroupList: Flow<ImmutableList<GroupProfile>>
    fun refreshJoinedGroupList()
    suspend fun joinGroup(groupId: String): ActionResult
    suspend fun quitGroup(groupId: String): ActionResult
    suspend fun getGroupInfo(groupId: String): GroupProfile?
    suspend fun getGroupMemberList(groupId: String): ImmutableList<GroupMemberProfile>
    suspend fun setAvatar(groupId: String, avatarUrl: String): ActionResult
    suspend fun transferGroupOwner(groupId: String, newOwnerUserID: String): ActionResult
}