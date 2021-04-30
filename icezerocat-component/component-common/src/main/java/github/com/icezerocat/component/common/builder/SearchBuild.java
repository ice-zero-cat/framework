package github.com.icezerocat.component.common.builder;

import github.com.icezerocat.component.common.model.Param;
import github.com.icezerocat.component.common.utils.DaoUtil;
import github.com.icezerocat.component.common.utils.DateUtil;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by zmj
 * On 2019/8/28.
 *
 * @author 0.0.0
 */
@SuppressWarnings("all")
public class SearchBuild {

    public final static String AND = "and";
    public final static String OR = "or";
    public final static String LIKE = "like";
    public final static String OR_LIKE = "orLike";
    //日月日期
    public final static String DAY = "day";
    public final static String OR_DAY = "orDay";
    public final static String MONTH = "month";
    public final static String OR_MONTH = "orMonth";
    //大于、小于、等于
    public final static String GREATER = "greater";
    public final static String OR_GREATER = "orGreater";
    public final static String LESS = "less";
    public final static String OR_LESS = "orLess";
    public final static String IS_NOT_EQUAL = "isNotEqual";
    public final static String IS_EQUAL = "isEqual";
    //开始、结束时间
    public final static String START_TIME = "startTime";
    public final static String OR_START_TIME = "orStartTime";
    public final static String END_TIME = "endTime";
    //是否为null
    public final static String IS_NULL = "isNull";
    public final static String IS_NOT_NULL = "isNotNull";

    final private String hql;
    final private Object[] list;

    private SearchBuild(Builder builder) {
        this.hql = builder.hql.toString();
        this.list = builder.list.toArray();
    }

    public String getHql() {
        return hql;
    }

    public Object[] getList() {
        return list;
    }

    public static class Builder {
        private List<Object> list = new ArrayList<>();
        private StringBuilder hql = new StringBuilder();

        public Builder() {

        }

        public Builder(String tableName) {
            this.hql.append(" from ").append(tableName).append(" where 1 = ? ");
            list.add(1);
        }

        public Builder operation(String operation) {
            this.hql.insert(0, operation);
            return this;
        }

        /**
         * 设置搜索条件
         *
         * @param searchList 搜索条件
         * @return 更新builder
         */
        public Builder searchList(List<Param> searchList) {
            for (Param search : searchList) {
                //过滤value为空的查询条件，判断type是否为null类型的不拦截
                if (SearchBuild.IS_NULL.equals(search.getType()) || SearchBuild.IS_NOT_NULL.equals(search.getType())
                        || !StringUtils.isEmpty(search.getValue())) {
                    String methodName = search.getType() != null && !StringUtils.isEmpty(StringUtils.trimWhitespace(search.getType())) ? search.getType() : "and";
                    Builder builder = (Builder) DaoUtil.invoke(this, methodName, search);
                    this.hql = builder.hql;
                    this.list = builder.list;
                }
            }
            return this;
        }

        public Builder and(Param search) {
            this.hql.append(" and ").append(search.getField()).append(" = ").append("'").append(search.getValue()).append("'");
            return this;
        }

        public Builder or(Param search) {
            this.hql.append(" or ").append(search.getField()).append(" = ").append("'").append(search.getValue()).append("'");
            return this;
        }

        public Builder isNotEqual(Param search) {
            this.hql.append(" and ").append(search.getField()).append(" != ").append("'").append(search.getValue()).append("'");
            return this;
        }

        public Builder orIsNotEqual(Param search) {
            this.hql.append(" or ").append(search.getField()).append(" != ").append("'").append(search.getValue()).append("'");
            return this;
        }

        public Builder andLike(Param search) {
            this.hql.append(" and ").append(search.getField()).append(" like").append(" '%").append(search.getValue()).append("%'");
            return this;
        }

        public Builder orLike(Param search) {
            this.hql.append(" or ").append(search.getField()).append(" like").append(" '%").append(search.getValue()).append("%'");
            return this;
        }

        public Builder like(Param search) {
            this.andLike(search);
            return this;
        }

        public Builder day(Param search) {
            return this.andDay(search);
        }

        public Builder andDay(Param search) {
            Date leftDate = formatDate(search);
            Calendar rightDate = Calendar.getInstance();
            rightDate.setTime(leftDate);
            //天数自增
            rightDate.add(Calendar.DAY_OF_MONTH, 1);

            this.hql.append(" and ").append(search.getField()).append(" >=").append(" ?").append(" and ").append(search.getField()).append(" <").append(" ?");
            this.list.add(leftDate);
            this.list.add(rightDate.getTime());
            return this;
        }

        public Builder orDay(Param search) {
            Date leftDate = formatDate(search);
            Calendar rightDate = Calendar.getInstance();
            rightDate.setTime(leftDate);
            //天数自增
            rightDate.add(Calendar.DAY_OF_MONTH, 1);

            this.hql.append(" or ").append(search.getField()).append(" >=").append(" ?").append(" and ").append(search.getField()).append(" <").append(" ?");
            this.list.add(leftDate);
            this.list.add(rightDate.getTime());
            return this;
        }


        public Builder startTime(Param search) {
            Date startDate = formatDate(search);
            this.hql.append(" and ").append(search.getField()).append(" >= ").append(" ?");
            this.list.add(startDate);
            return this;
        }

        public Builder orStartTime(Param search) {
            Date startDate = formatDate(search);
            this.hql.append(" or ").append(search.getField()).append(" >= ").append(" ?");
            this.list.add(startDate);
            return this;
        }

        public Builder endTime(Param search) {
            Date endDate = formatDate(search);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(endDate);
            calendar.add(Calendar.DAY_OF_MONTH, 1);
            this.hql.append(" and ").append(search.getField()).append(" < ").append(" ?");
            this.list.add(calendar.getTime());
            return this;
        }

        public Builder month(Param search) {
            String yearMonth = (String) search.getValue();
            String[] strings = yearMonth.split("-");
            this.list.add(DateUtil.getFirstDayOfMonth(Integer.valueOf(strings[0]), Integer.valueOf(strings[1])));
            this.list.add(DateUtil.getLastDayOfMonth(Integer.valueOf(strings[0]), Integer.valueOf(strings[1])));
            this.hql.append(" and ").append(search.getField()).append(" >=").append(" ?").append(" and ").append(search.getField()).append(" <=").append(" ?");
            return this;
        }

        public Builder andMonth(Param search) {
            this.month(search);
            return this;
        }

        public Builder orMonth(Param search) {
            String yearMonth = (String) search.getValue();
            String[] strings = yearMonth.split("-");
            this.list.add(DateUtil.getFirstDayOfMonth(Integer.valueOf(strings[0]), Integer.valueOf(strings[1])));
            this.list.add(DateUtil.getLastDayOfMonth(Integer.valueOf(strings[0]), Integer.valueOf(strings[1])));
            this.hql.append(" or ").append(search.getField()).append(" >=").append(" ?").append(" and ").append(search.getField()).append(" <=").append(" ?");
            return this;
        }

        public Builder greater(Param search) {
            this.hql.append(" and ").append(search.getField()).append(" < ").append(search.getValue());
            return this;
        }

        public Builder orGreater(Param search) {
            this.hql.append(" or ").append(search.getField()).append(" < ").append(search.getValue());
            return this;
        }

        public Builder lest(Param search) {
            this.hql.append(" and ").append(search.getField()).append(" > ").append(search.getValue());
            return this;
        }

        public Builder orLest(Param search) {
            this.hql.append(" or ").append(search.getField()).append(" > ").append(search.getValue());
            return this;
        }

        public Builder isNull(Param search) {
            this.hql.append(" and ").append(search.getField()).append(" IS NULL ");
            return this;
        }

        public Builder isNotNull(Param search) {
            this.hql.append(" and ").append(search.getField()).append(" IS NOT NULL ");
            return this;
        }

        public SearchBuild start() {
            return new SearchBuild(this);
        }

        public Date formatDate(Param search) {
            String formatDate = search.getFormatDate() != null && !StringUtils.isEmpty(StringUtils.trimWhitespace(search.getFormatDate())) ? search.getFormatDate() : "yyyy-MM-dd";
            return DateUtil.parseDate(String.valueOf(search.getValue()), formatDate);
        }
    }
}
