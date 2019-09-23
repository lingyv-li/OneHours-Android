package com.guanmu.onehours.model


import com.guanmu.onehours.*
import com.guanmu.onehours.ObjectUtils.coalesce
import java.util.*

/**
 * Created by Larry.
 */
class MTask private constructor(
        private val daoSession: DaoSession, val GUID: UUID? = UUID.randomUUID(), date: Date? = null, var usedTime: Long = 0, var count: Int = 0) {
    private val dao: DTaskDao = daoSession.dTaskDao
    private val pointDao: DLinkTaskPointDao = daoSession.dLinkTaskPointDao

    var date: Date? = null

    val d: DTask
        get() = dao.load(this.GUID.toString())

    val pointList: List<MPoint>
        get() = MPoint.listFrom(daoSession, d.dPointList)

    constructor(dao: DaoSession, GUID: UUID) : this(dao,
            coalesce<DTask>(dao.dTaskDao.load(GUID.toString()),
                    DTask(GUID.toString()))
    )

    private constructor(dao: DaoSession, dTask: DTask?) : this(dao,
            UUID.fromString(if (dTask != null) dTask.guid else UUID.randomUUID().toString()),
            dTask?.date,
            if (dTask != null) dTask.usedTime else 0,
            if (dTask != null) dTask.count else 0
    ) {
    }

    init {

        this.date = date ?: Calendar.getInstance().time
    }

    fun addPoint(vararg points: MPoint) {
        addPoint(Arrays.asList(*points))
    }

    fun addPoint(points: List<MPoint>) {
        val qb = pointDao.queryBuilder().where(DLinkTaskPointDao.Properties.TaskId.eq(this.GUID.toString()))
        for (point in points) {
            if (qb.where(DLinkTaskPointDao.Properties.PointId.eq(point.guid!!.toString())).count() == 0L) {
                val link = DLinkTaskPoint()
                link.taskId = this.GUID.toString()
                link.pointId = point.guid!!.toString()
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
                .where(DLinkTaskPointDao.Properties.TaskId.eq(this.GUID.toString()))
                .where(DLinkTaskPointDao.Properties.PointId.`in`(pointsId))
                .buildDelete().executeDeleteWithoutDetachingEntities()
    }

    fun save() {
        var dTask: DTask? = dao.load(GUID.toString())
        if (dTask == null) {
            dTask = DTask(GUID.toString())
            dao.insert(dTask)
        }
        dTask.setCount(count)
        dTask.date = date
        dTask.setUsedTime(usedTime)
        dTask.lastUpdated = Date()

    }

    companion object {

        /**
         * @param dao    DaoSession
         * @param create Whether create a new task if no task found
         * @return Task today. Null if not found and create set to false
         */
        fun getToday(dao: DaoSession, create: Boolean): MTask? {
            try {
                val dTask = dao.dTaskDao.queryBuilder().where(DTaskDao.Properties.Date.gt(DateUtils.today)).orderDesc(DTaskDao.Properties.Date).list()[0]
                return if (dTask.dLinkPointList.isEmpty() && create) {
                    createTask(dao)
                } else {
                    MTask(dao, dTask)
                }
            } catch (e: IndexOutOfBoundsException) {
                return if (create) {
                    createTask(dao)
                } else {
                    null
                }
            }

        }

        private fun createTask(dao: DaoSession): MTask? {
            val mPoints = MPoint.loadAscProf(dao, SharePreferenceUtils.instance.targetCount)
            if (!mPoints.isEmpty()) {
                val task = MTask(dao)
                task.addPoint(mPoints)
                task.save()
                return task
            } else {
                return null
            }
        }
    }
}
