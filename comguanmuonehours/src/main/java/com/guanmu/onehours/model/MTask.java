package com.guanmu.onehours.model;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.guanmu.onehours.DLinkTaskPoint;
import com.guanmu.onehours.DLinkTaskPointDao;
import com.guanmu.onehours.DTask;
import com.guanmu.onehours.DTaskDao;
import com.guanmu.onehours.DaoSession;
import com.guanmu.onehours.DateUtils;
import com.guanmu.onehours.SharePreferenceUtils;

import org.greenrobot.greendao.query.QueryBuilder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static com.guanmu.onehours.ObjectUtils.coalesce;

/**
 * Created by Larry.
 */
public class MTask {

    private DaoSession daoSession;
    private DTaskDao dao;
    private DLinkTaskPointDao pointDao;

    private UUID GUID;
    private Date date;
    private long usedTime;
    private int count;

    private MTask(DaoSession dao) {
        this(dao, UUID.randomUUID(), null, 0, 0);
    }

    public MTask(DaoSession dao, @NonNull UUID GUID) {
        this(dao,
                coalesce(dao.getDTaskDao().load(GUID.toString()),
                        new DTask(GUID.toString()))
        );
    }

    private MTask(DaoSession dao, @Nullable DTask dTask) {
        this(dao,
                UUID.fromString(dTask != null ? dTask.getGUID() : UUID.randomUUID().toString()),
                dTask != null ? dTask.getDate() : null,
                dTask != null ? dTask.getUsedTime() : 0,
                dTask != null ? dTask.getCount() : 0
        );
    }

    private MTask(DaoSession dao, @Nullable UUID GUID, @Nullable Date date, long usedTime, int count) {
        this.daoSession = dao;
        this.dao = dao.getDTaskDao();
        this.pointDao = dao.getDLinkTaskPointDao();

        this.GUID = GUID != null ? GUID : UUID.randomUUID();
        this.date = date != null ? date : Calendar.getInstance().getTime();
        this.usedTime = usedTime;
        this.count = count;
    }

    /**
     * @param dao    DaoSession
     * @param create Whether create a new task if no task found
     * @return Task today. Null if not found and create set to false
     */
    @Nullable
    public static MTask getToday(DaoSession dao, boolean create) {
        try {
            DTask dTask = dao.getDTaskDao().queryBuilder().where(DTaskDao.Properties.Date.gt(DateUtils.getToday())).orderDesc(DTaskDao.Properties.Date).list().get(0);
            if (dTask.getDLinkPointList().isEmpty() && create) {
                return createTask(dao);
            } else {
                return new MTask(dao, dTask);
            }
        } catch (IndexOutOfBoundsException e) {
            if (create) {
                return createTask(dao);
            } else {
                return null;
            }
        }
    }

    private static MTask createTask(DaoSession dao) {
        List<MPoint> mPoints = MPoint.loadAscProf(dao, SharePreferenceUtils.getInstance().getTargetCount());
        if (!mPoints.isEmpty()) {
            MTask task = new MTask(dao);
            task.addPoint(mPoints);
            task.save();
            return task;
        } else {
            return null;
        }
    }

    public DTask getD() {
        return dao.load(this.getGUID().toString());
    }

    public UUID getGUID() {
        return GUID;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public long getUsedTime() {
        return usedTime;
    }

    public void setUsedTime(long usedTime) {
        this.usedTime = usedTime;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public List<MPoint> getPointList() {
        return MPoint.listFrom(daoSession, getD().getDPointList());
    }

    public void addPoint(@NonNull MPoint... points) {
        addPoint(Arrays.asList(points));
    }

    public void addPoint(@NonNull List<MPoint> points) {
        QueryBuilder qb = pointDao.queryBuilder().where(DLinkTaskPointDao.Properties.TaskId.eq(this.GUID.toString()));
        for (MPoint point :
                points) {
            if (qb.where(DLinkTaskPointDao.Properties.PointId.eq(point.getGUID().toString())).count() == 0) {
                DLinkTaskPoint link = new DLinkTaskPoint();
                link.setTaskId(this.GUID.toString());
                link.setPointId(point.getGUID().toString());
                pointDao.save(link);
            }
        }

    }

    public void removePoint(@NonNull MPoint... points) {
        removePoint(Arrays.asList(points));
    }

    public void removePoint(@NonNull List<MPoint> points) {
        List<String> pointsId = new ArrayList<>();
        for (MPoint point :
                points) {
            pointsId.add(point.getGUID().toString());
        }
        pointDao.queryBuilder()
                .where(DLinkTaskPointDao.Properties.TaskId.eq(this.GUID.toString()))
                .where(DLinkTaskPointDao.Properties.PointId.in(pointsId))
                .buildDelete().executeDeleteWithoutDetachingEntities();
    }

    public void save() {
        DTask dTask = dao.load(GUID.toString());
        if (dTask == null) {
            dTask = new DTask(GUID.toString());
            dao.insert(dTask);
        }
        dTask.setCount(count);
        dTask.setDate(date);
        dTask.setUsedTime(usedTime);
        dTask.setLastUpdated(new Date());

    }
}
