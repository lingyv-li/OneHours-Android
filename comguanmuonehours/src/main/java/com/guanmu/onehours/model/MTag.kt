package com.guanmu.onehours.model

import com.guanmu.onehours.*
import java.util.*

/**
 * Created by Larry.
 */
class MTag constructor(private val daoSession: DaoSession, name: String?, var belong: String?) {
    private val dao: DTagDao = daoSession.dTagDao
    private val pointDao: DLinkTagPointDao = daoSession.dLinkTagPointDao

    var name: String = ""

    private val parent: MTag? = null
    private val children: List<MTag>
    private val points: List<MPoint>

    val guid: UUID
        get() = UUID.fromString(d.guid)

    private val d: DTag
        get() {
            val GUID = tryGetGUID(dao, name, belong)
            return if (GUID != null) {
                dao.load(GUID.toString())
            } else {
                val dTag = DTag(UUID.randomUUID().toString())
                dTag.name = name
                dTag.belong = belong
                if (parent != null) {
                    dTag.parent = parent.d
                }
                dao.insert(dTag)
                dTag
            }
        }

    private constructor(dao: DaoSession, dTag: DTag) : this(dao,
            dTag.name,
            dTag.belong
    )

    init {
        this.name = name ?: ""

        children = ArrayList()
        points = ArrayList()
        save()
    }

    fun getPoints(): List<MPoint> {
        return MPoint.listFrom(daoSession, d.dPointList)
    }

    fun setPoint(vararg points: MPoint) {
        setPoint(listOf(*points))
    }

    fun setPoint(points: List<MPoint>) {
        removePoint(MPoint.listFrom(daoSession, d.dPointList))
        addPoint(points)
    }

    fun addPoint(vararg points: MPoint) {
        addPoint(listOf(*points))
    }

    fun addPoint(points: List<MPoint>) {
        val dTag = d
        val qb = pointDao.queryBuilder().where(DLinkTagPointDao.Properties.TagId.eq(dTag.guid))
        for (point in points) {
            if (qb.where(DLinkTagPointDao.Properties.PointId.eq(point.guid!!.toString())).count() == 0L) {
                val link = DLinkTagPoint()
                link.pointId = point.guid!!.toString()
                link.tagId = dTag.guid
                pointDao.save(link)
            }
        }
    }

    fun removePoint(vararg points: MPoint) {
        removePoint(listOf(*points))
    }

    fun removePoint(points: List<MPoint>) {
        val pointsId = ArrayList<String>()
        for (point in points) {
            pointsId.add(point.guid!!.toString())
        }
        pointDao.queryBuilder()
                .where(DLinkTagPointDao.Properties.TagId.eq(d.guid))
                .where(DLinkTagPointDao.Properties.PointId.`in`(pointsId))
                .buildDelete().executeDeleteWithoutDetachingEntities()
    }

    fun getChildren(): List<MTag> {
        return listFrom(daoSession, d.children)
    }

    fun setChildren(children: List<MTag>) {
        removeChildren(listFrom(daoSession, d.children))
        addChildren(children)
    }

    fun setChildren(vararg children: MTag) {
        setChildren(listOf(*children))
    }

    fun addChildren(vararg children: MTag) {
        addChildren(listOf(*children))
    }

    fun addChildren(children: List<MTag>) {
        for (child in children) {
            child.setParent(this)
        }
    }

    fun removeChildren(vararg children: MTag) {
        removeChildren(listOf(*children))
    }

    fun removeChildren(children: List<MTag>) {
        for (child in children) {
            if (child.getParent() === this) {
                child.setParent(null)
            }
        }
    }

    fun getParent(): MTag {
        return MTag(daoSession, d.parent)
    }

    fun setParent(parent: MTag?) {
        val dTag = d
        if (parent != null) {
            dTag.parentId = parent.d.guid
        }
    }

    fun save() {
        val dTag = d
        dTag.name = name
        dTag.belong = belong
        dao.save(dTag)
    }

    fun delete() {
        val GUID = tryGetGUID(dao, name, belong)
        if (GUID != null) {
            dao.deleteByKey(GUID.toString())
        }
    }

    override fun toString(): String {
        return name
    }

    companion object {

        @JvmOverloads
        fun loadAll(dao: DaoSession, filterBelong: Boolean = false, belong: String? = null): List<MTag> {
            cleanUnrelated(dao)
            var qb = dao.dTagDao.queryBuilder()
            if (filterBelong) {
                qb = qb.where(if (belong == null) {
                    DTagDao.Properties.Belong.isNull
                } else {
                    DTagDao.Properties.Belong.eq(belong)
                })
            }
            return listFrom(dao, qb.list())
        }

        private fun cleanUnrelated(dao: DaoSession) {
            for (dTag in dao.dTagDao.loadAll()) {
                if (dTag.dLinkPointList.isEmpty()) {
                    dTag.delete()
                }
            }
        }


        private fun tryGetGUID(dao: DTagDao, name: String?, belong: String?): UUID? {
            return try {
                var qb = dao.queryBuilder().where(DTagDao.Properties.Name.eq(name))
                qb = qb.where(if (belong == null) {
                    DTagDao.Properties.Belong.isNull
                } else {
                    DTagDao.Properties.Belong.eq(belong)
                })
                UUID.fromString(qb.unique().guid)
            } catch (ignored: NullPointerException) {
                null
            }

        }

        internal fun listFrom(daoSession: DaoSession, dTagList: List<DTag>?): List<MTag> {
            val result = ArrayList<MTag>()
            if (dTagList != null) {
                for (dTag in dTagList) {
                    result.add(MTag(daoSession, dTag))
                }
            }
            return result
        }
    }
}
