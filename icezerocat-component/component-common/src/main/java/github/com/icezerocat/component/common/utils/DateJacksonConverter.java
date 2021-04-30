package github.com.icezerocat.component.common.utils;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;

import java.io.IOException;
import java.text.ParseException;
import java.util.Date;

/**
 * 自定义Jackson反序列化日期类型时应用的类型转换器,一般用于@RequestBody接受参数时使用
 * <p>
 * Created by zmj
 * On 2019/12/30.
 *
 * @author 0.0.0
 */
@SuppressWarnings("unused")
public class DateJacksonConverter extends JsonDeserializer<Date> {

    private static String[] pattern =
            new String[]{"yyyy-MM-dd", "yyyy-MM-dd HH:mm", "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd HH:mm:ss.S",
                    "yyyy.MM.dd", "yyyy.MM.dd HH:mm", "yyyy.MM.dd HH:mm:ss", "yyyy.MM.dd HH:mm:ss.S",
                    "yyyy/MM/dd", "yyyy/MM/dd HH:mm", "yyyy/MM/dd HH:mm:ss", "yyyy/MM/dd HH:mm:ss.S",
                    /* RFC 1123 with 2-digit Year */"EEE, dd MMM yy HH:mm:ss z",
                    /* RFC 1123 with 4-digit Year */"EEE, dd MMM yyyy HH:mm:ss z",
                    /* RFC 1123 with no Timezone */"EEE, dd MMM yy HH:mm:ss",
                    /* Variant of RFC 1123 */"EEE, MMM dd yy HH:mm:ss",
                    /* RFC 1123 with no Seconds */"EEE, dd MMM yy HH:mm z",
                    /* Variant of RFC 1123 */"EEE dd MMM yyyy HH:mm:ss",
                    /* RFC 1123 with no Day */"dd MMM yy HH:mm:ss z",
                    /* RFC 1123 with no Day or Seconds */"dd MMM yy HH:mm z",
                    /* ISO 8601 slightly modified */"yyyy-MM-dd'T'HH:mm:ssZ",
                    /* ISO 8601 slightly modified */"yyyy-MM-dd'T'HH:mm:ss'Z'",
                    /* ISO 8601 slightly modified */"yyyy-MM-dd'T'HH:mm:sszzzz",
                    /* ISO 8601 slightly modified */"yyyy-MM-dd'T'HH:mm:ss z",
                    /* ISO 8601 */"yyyy-MM-dd'T'HH:mm:ssz",
                    /* ISO 8601 slightly modified */"yyyy-MM-dd'T'HH:mm:ss.SSSz",
                    /* ISO 8601 slightly modified */"yyyy-MM-dd'T'HHmmss.SSSz",
                    /* ISO 8601 slightly modified */"yyyy-MM-dd'T'HH:mm:ss",
                    /* ISO 8601 w/o seconds */"yyyy-MM-dd'T'HH:mmZ",
                    /* ISO 8601 w/o seconds */"yyyy-MM-dd'T'HH:mm'Z'",
                    /* RFC 1123 without Day Name */"dd MMM yyyy HH:mm:ss z",
                    /* RFC 1123 without Day Name and Seconds */"dd MMM yyyy HH:mm z",
                    /* Simple Date Format */"yyyy-MM-dd",
                    /* Simple Date Format */"MMM dd, yyyy"};

    @Override
    public Date deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        Date targetDate = null;
        String originDate = p.getText();
        if (StringUtils.isNotBlank(originDate)) {
            try {
                long longDate = Long.parseLong(originDate.trim());
                targetDate = new Date(longDate);
            } catch (NumberFormatException e) {
                try {
                    targetDate = DateUtils.parseDate(originDate, DateJacksonConverter.pattern);
                } catch (ParseException pe) {
                    try {
                        targetDate = DateUtil.parseDate(originDate);
                    } catch (Exception e1) {
                        throw new IOException(String.format(
                                "'%s' can not convert to type 'java.util.Date',just support timestamp(type of long) and following date format(%s)",
                                originDate,
                                StringUtils.join(pattern, ",")));
                    }
                }
            }
        }
        return targetDate;
    }

    @Override
    public Class<?> handledType() {
        return Date.class;
    }
}
