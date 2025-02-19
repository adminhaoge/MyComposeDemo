package com.funny.translation.translate.database

import androidx.annotation.Keep
import androidx.compose.runtime.Stable
import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.TypeConverter
import androidx.room.Upsert
import com.funny.translation.helper.JsonX
import com.funny.translation.translate.ui.long_text.EditablePrompt
import com.funny.translation.translate.ui.long_text.Term
import kotlinx.coroutines.flow.Flow

private fun now() = System.currentTimeMillis()

@Keep
@Entity(tableName = "table_long_text_trans_tasks")
@Stable
data class LongTextTransTask(
    @PrimaryKey
    val id: String,
    @ColumnInfo
    val chatBotId: Int,
    @ColumnInfo
    val sourceText: String,
    @ColumnInfo
    val resultText: String = "",
    @ColumnInfo
    val prompt: EditablePrompt,
    @ColumnInfo
    val allCorpus: List<Term>,
    @ColumnInfo
    val sourceTextSegments: List<Int>,
    @ColumnInfo
    val resultTextSegments: List<Int>,
    @ColumnInfo
    val translatedLength: Int = 0,

    @ColumnInfo
    val remark: String = "",
    @ColumnInfo
    val createTime: Long = now(),
    @ColumnInfo
    val updateTime: Long = now(),
) {
    val finishTranslating: Boolean
        get() = translatedLength == sourceText.length

    val translatedProgress
        get() = (translatedLength.toFloat() / sourceText.length.toFloat()).coerceIn(0f, 1f)
}

// 不带具体文本，只包括少量信息的 LongTextTransTask，以加快列表时的查询速度
data class LongTextTransTaskMini(
    val id: String,
    val chatBotId: Int,
    val translatedLength: Int = 0,
    val remark: String = "",
    val createTime: Long = now(),
    val updateTime: Long = now(),

    val sourceTextLength: Int,
) {
    val finishTranslating: Boolean
        get() = translatedLength >= sourceTextLength

    val translatedProgress
        get() = (translatedLength.toFloat() / sourceTextLength.toFloat()).coerceIn(0f, 1f)
}

@Dao
interface LongTextTransDao {
    @Query("select * from table_long_text_trans_tasks where id = :id")
    fun getById(id: String): LongTextTransTask?

    @Query("select * from table_long_text_trans_tasks")
    fun getAll(): Flow<List<LongTextTransTask>>

    @Query("select id, chatBotId, translatedLength, remark, createTime, updateTime, length(sourceText) as `sourceTextLength` from table_long_text_trans_tasks")
    fun getAllMini(): Flow<List<LongTextTransTaskMini>>

    @Query("delete from table_long_text_trans_tasks where id = :id")
    fun deleteById(id: String)

    @Upsert
    fun upsert(task: LongTextTransTask)

    @Insert
    fun insert(task: LongTextTransTask)

    // updateAllCorpus
    @Query("update table_long_text_trans_tasks set allCorpus = :allCorpus where id = :id")
    fun updateAllCorpus(id: String, allCorpus: List<Pair<String, String>>)

    @Query("update table_long_text_trans_tasks set sourceText = :text where id = :id")
    fun updateSourceText(id: String, text: String)

    @Query("update table_long_text_trans_tasks set remark = :remark where id = :id")
    fun updateRemark(id: String, remark: String)
}

class TermListConverter {
    @TypeConverter
    fun stringToObject(value: String): List<Term> {
        return JsonX.fromJson(value)
    }

    @TypeConverter
    fun objectToString(list: List<Term>): String {
        return JsonX.toJson(list)
    }
}

class EditablePromptConverter {
    @TypeConverter
    fun stringToObject(value: String): EditablePrompt {
        return JsonX.fromJson(value)
    }

    @TypeConverter
    fun objectToString(list: EditablePrompt): String {
        return JsonX.toJson(list)
    }
}

class IntListConverter {
    @TypeConverter
    fun stringToObject(value: String): List<Int> {
        return JsonX.fromJson(value)
    }

    @TypeConverter
    fun objectToString(list: List<Int>): String {
        return JsonX.toJson(list)
    }
}