package com.guanmu.onehours.model

import android.text.TextUtils
import com.guanmu.onehours.*
import com.guanmu.onehours.ObjectUtils.coalesce
import java.util.*

/**
 * Created by Larry.
 */
class MPoint private constructor(private val daoSession: DaoSession, GUID: UUID?, var averageTime: Long, var belong: String?, content: String?, var count: Int, metaContent: String?, var proficiency: Int, type: Type?) {
    private val dao: DPointDao = daoSession.dPointDao
    private val tagDao: DLinkTagPointDao = daoSession.dLinkTagPointDao

    var guid: UUID? = null
    var content: String? = null
    var metaContent: String? = null
    var type: Type? = null
    private val addTagList: MutableList<MTag>
    private var removeTagList: MutableList<MTag>? = null

    private val d: DPoint?
        get() = dao.load(guid!!.toString())

    constructor(dao: DaoSession) : this(dao, UUID.randomUUID(), 0, null, null, 0, null, 0, null) {}

    constructor(dao: DaoSession, GUID: UUID) : this(dao,
            coalesce<DPoint>(dao.dPointDao.load(GUID.toString()),
                    DPoint(GUID.toString()))
    )

    private constructor(dao: DaoSession, dPoint: DPoint?) : this(dao,
            UUID.fromString(if (dPoint != null) dPoint.guid else UUID.randomUUID().toString()),
            dPoint?.averageTime ?: 0,
            dPoint?.belong,
            dPoint?.content,
            dPoint?.count ?: 0,
            dPoint?.metaContent,
            dPoint?.proficiency ?: 0,
            dPoint?.type
    )

    init {

        this.guid = GUID ?: UUID.randomUUID()
        this.content = content ?: ""
        this.metaContent = metaContent ?: ""
        this.type = type ?: Type.normal

        this.addTagList = ArrayList()
        this.removeTagList = ArrayList()
    }

    fun getAddTagList(): List<MTag> {
        return if (d != null) {
            MTag.listFrom(daoSession, d!!.dTagList)
        } else {
            this.addTagList
        }
    }


    fun setTag(vararg tags: MTag) {
        setTag(Arrays.asList(*tags))
    }

    fun setTag(tags: List<MTag>) {
        if (d != null) {
            removeTag(MTag.listFrom(daoSession, d!!.dTagList))
            addTag(tags)
        } else {
            this.addTagList.clear()
            this.addTagList.addAll(tags)
            this.removeTagList = ArrayList()
        }
    }

    fun addTag(vararg tags: MTag) {
        addTag(Arrays.asList(*tags))
    }

    fun addTag(tags: List<MTag>) {
        if (d != null) {
            val qb = tagDao.queryBuilder().where(DLinkTagPointDao.Properties.PointId.eq(this.guid!!.toString()))
            for (tag in tags) {
                if (qb.where(DLinkTagPointDao.Properties.TagId.eq(tag.guid.toString())).count() == 0L) {
                    val link = DLinkTagPoint()
                    link.pointId = this.guid!!.toString()
                    link.tagId = tag.guid.toString()
                    tagDao.save(link)
                }
            }
        } else {
            this.addTagList.addAll(tags)
        }
    }


    fun removeTag(vararg tags: MTag) {
        removeTag(Arrays.asList(*tags))
    }

    fun removeTag(tags: List<MTag>) {
        if (d != null) {
            val tagsId = ArrayList<String>()
            for (tag in tags) {
                tagsId.add(tag.guid.toString())
            }
            tagDao.queryBuilder()
                    .where(DLinkTagPointDao.Properties.PointId.eq(this.guid!!.toString()))
                    .where(DLinkTagPointDao.Properties.TagId.`in`(tagsId))
                    .buildDelete().executeDeleteWithoutDetachingEntities()
            // TODO clean not related objects
        } else {
            this.removeTagList!!.addAll(tags)
        }
    }

    fun save() {
        var dPoint: DPoint? = dao.load(guid!!.toString())
        if (dPoint == null) {
            dPoint = DPoint(guid!!.toString())
            dao.insert(dPoint)

            addTag(this.addTagList)
            removeTag(this.removeTagList!!)
        }
        dPoint.averageTime = averageTime
        dPoint.belong = belong
        dPoint.content = content
        dPoint.setCount(count)
        dPoint.lastUpdated = Date()
        dPoint.metaContent = metaContent
        dPoint.proficiency = proficiency
        dPoint.type = type
        dPoint.syncState = DPoint.SyncState.changed
        dao.save(dPoint)
    }

    fun delete() {
        dao.deleteByKey(guid!!.toString())
    }

    enum class Type {
        normal, image
    }

    companion object {

        fun loadAscProf(dao: DaoSession, n: Int): List<MPoint> {
            val qb = dao.dPointDao.queryBuilder()
                    .where(DPointDao.Properties.Belong.isNull)
                    .orderAsc(DPointDao.Properties.Proficiency)
                    .limit(n)
            return listFrom(dao, qb.list())

        }

        @JvmOverloads
        fun loadAll(dao: DaoSession, searchString: String? = null, filterBelong: Boolean = false, belong: String? = null): List<MPoint> {
            var qb = dao.dPointDao.queryBuilder()
            if (filterBelong) {
                if (belong == null) {
                    qb = qb.where(DPointDao.Properties.Belong.isNull)
                } else {
                    qb = qb.where(DPointDao.Properties.Belong.eq(belong))
                }
            }
            if (!TextUtils.isEmpty(searchString)) {
                qb = qb.where(DPointDao.Properties.Content.like("%" + searchString!!.replace(' ', '%') + "%"))
            }
            return listFrom(dao, qb.list())
        }

        fun countAll(dao: DaoSession): Long {
            return dao.dPointDao.queryBuilder().where(DPointDao.Properties.Belong.isNull).count()
        }

        fun countProf(dao: DaoSession): Long {
            return dao.dPointDao.queryBuilder().where(
                    DPointDao.Properties.Belong.isNull,
                    DPointDao.Properties.Proficiency.lt(5)
            ).count()
        }

        internal fun listFrom(dao: DaoSession, dPointList: List<DPoint>): List<MPoint> {
            val result = ArrayList<MPoint>()
            for (dPoint in dPointList) {
                result.add(MPoint(dao, dPoint))
            }
            return result
        }
    }
}
