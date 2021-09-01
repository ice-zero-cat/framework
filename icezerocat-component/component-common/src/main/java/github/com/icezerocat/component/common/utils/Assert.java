package github.com.icezerocat.component.common.utils;

import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * 断言工具类
 *
 * <dl>
 *     <dt><b>ChangeLog:</b></dt>
 *     <dd>1.0.0@2019-07-18 10:42 - The Very First Version.</dd>
 *     <dd>1.0.1@2020-07-21 13:49 - Add function: {@link Assert#assignable(Class, Class, Supplier)}</dd>
 * </dl>
 *
 * @author zero
 * @version 1.0.1
 * date 2019-07-18 10:42
 */
@SuppressWarnings("unused")
public final class Assert {
    private static final String DEFAULT_PARAMETER_NOT_EMPTY_EXCEPTION_MSG = "参数为空! (null or empty)";
    private static final String DEFAULT_COLLECTION_NOT_EMPTY_EXCEPTION_MSG = "集合为空! (null or empty)";

    private Assert() {
    }

    /**
     * Description: 判断 clazz 是否和 toClazz 相同, 或者是否是 toClazz 的超类或接口
     *
     * @param clazz              当前 Class 对象
     * @param toClazz            待判断的 Class 对象
     * @param exceptionSupplier  如果不满足, 抛出的异常 {@link Supplier}
     * @param <GenericException> 通用异常
     * @throws GenericException 通用异常
     */
    public static <GenericException extends Throwable> void assignable(Class<?> clazz, Class<?> toClazz, Supplier<? extends GenericException> exceptionSupplier)
            throws GenericException {
        if (!clazz.isAssignableFrom(toClazz)) {
            throw exceptionSupplier.get();
        }
    }


    //-- Boolean --//

    /**
     * 通用条件断言
     *
     * @param any                任意对象
     * @param anyPredicate       任意对象的条件判定
     * @param exceptionSupplier  异常 {@link Supplier}
     * @param <GenericAny>       通用任意
     * @param <GenericException> 通用异常
     * @return GenericAny
     * @throws GenericException 通用异常
     */
    public static <GenericAny, GenericException extends Throwable> GenericAny predicate(GenericAny any, Predicate<GenericAny> anyPredicate, Supplier<? extends GenericException> exceptionSupplier) throws GenericException {
        if (!nonNull(anyPredicate).test(nonNull(any))) {
            throw exceptionSupplier.get();
        }
        return any;
    }

    /**
     * Description: 执行条件断言<br>
     * Details: 如果 anyPredicate 不满足, 则执行 thenFunction
     *
     * @param any              任意对象
     * @param anyPredicate     任意对象的条件判定
     * @param thenFunction     anyPredicate 不满足的时候执行的 {@link Function}
     * @param <GenericAny>断言判断
     * @return GenericAny 断言判断
     */
    public static <GenericAny> GenericAny predicate(GenericAny any, Predicate<GenericAny> anyPredicate, Function<GenericAny, GenericAny> thenFunction) {
        if (!nonNull(anyPredicate).test(nonNull(any))) {
            return thenFunction.apply(any);
        }
        return any;
    }

    /**
     * Description: 假 断言
     *
     * @param bool               boolean
     * @param operationWhenFalse {@link Runnable} 为假时执行
     * @return boolean
     */
    public static Boolean isFalse(Boolean bool, Runnable operationWhenFalse) {
        if (!bool) {
            nonNull(operationWhenFalse).run();
        }
        return bool;
    }

    /**
     * Description: 真 断言
     *
     * @param bool              boolean
     * @param operationWhenTrue {@link Runnable} 为真时执行
     * @return boolean
     */
    public static Boolean isTrue(Boolean bool, Runnable operationWhenTrue) {
        if (bool) {
            nonNull(operationWhenTrue).run();
        }
        return bool;
    }


    //-- Object --//

    /**
     * Description: 真 断言
     *
     * @param bool                       boolean
     * @param exceptionSupplierWhenFalse 异常 {@link Supplier}
     * @param <GenericException>         通用异常
     * @throws GenericException 通用异常
     */
    public static <GenericException extends Throwable> void isTrue(Boolean bool, Supplier<? extends GenericException> exceptionSupplierWhenFalse) throws GenericException {
        if (!bool) {
            throw exceptionSupplierWhenFalse.get();
        }
    }

    /**
     * Description: 真 断言
     *
     * @param bool                       boolean
     * @param operationWhenTrue          {@link Runnable} 为真时执行
     * @param exceptionSupplierWhenFalse 异常 {@link Supplier}
     * @param <GenericException>         通用异常
     * @throws GenericException 通用异常
     */
    public static <GenericException extends Throwable> void isTrue(Boolean bool, Runnable operationWhenTrue, Supplier<? extends GenericException> exceptionSupplierWhenFalse) throws GenericException {
        if (!isTrue(bool, operationWhenTrue)) {
            throw exceptionSupplierWhenFalse.get();
        }
    }

    /**
     * 非空断言
     *
     * @param object          判定目标的
     * @param <GenericTarget> 目标对象 (如果非空)
     * @return 判断目标
     */
    public static <GenericTarget> GenericTarget nonNull(GenericTarget object) {
        return Optional.ofNullable(object).orElseThrow(() -> new NullPointerException(DEFAULT_PARAMETER_NOT_EMPTY_EXCEPTION_MSG));
    }


//    /**
//     * Description: 非空断言
//     *
//     * @param optional          判定目标的 {@link Optional}
//     * @param exceptionSupplier 异常 Supplier
//     * @return GenericTarget 目标对象 (如果非空)
//     * @author LiKe
//     * @date 2019-11-18 09:43:26
//     */
//    public static <GenericTarget, GenericException extends Throwable> GenericTarget nonNull(Optional<GenericTarget> optional, Supplier<? extends GenericException> exceptionSupplier) throws GenericException {
//        if (!optional.isPresent())
//            throw exceptionSupplier.get();
//        return optional.get();
//    }

    /**
     * Description: 非空断言
     *
     * @param object                 目标对象
     * @param exceptionMessage       异常信息
     * @param exceptionMessageParams 异常信息的参数
     * @param <GenericTarget>        目标对象
     * @return GenericTarget
     * @throws RuntimeException 如果 object 为 null
     */
    public static <GenericTarget> GenericTarget nonNull(GenericTarget object, String exceptionMessage, Object... exceptionMessageParams) {
        return Optional.ofNullable(object).orElseThrow(() -> new RuntimeException(String.format(exceptionMessage, exceptionMessageParams)));
    }


    //-- Collection --//

    /**
     * Description: 非空断言
     *
     * @param object             判定目标对象
     * @param exceptionSupplier  异常 Supplier
     * @param <GenericTarget>    目标对象
     * @param <GenericException> 通用异常
     * @return 目标对象 (如果非空)
     * @throws GenericException 通用异常
     */
    public static <GenericTarget, GenericException extends Throwable> GenericTarget nonNull(GenericTarget object, Supplier<? extends GenericException> exceptionSupplier) throws GenericException {
        if (Objects.isNull(object)) {
            throw exceptionSupplier.get();
        }
        return object;
    }

    /**
     * Description: 非空断言
     *
     * @param objectArr          判定目标对象数组
     * @param exceptionSupplier  异常 Supplier
     * @param <GenericTarget>    目标对象
     * @param <GenericException> 通用异常
     * @throws GenericException 通用异常
     */
    public static <GenericTarget, GenericException extends Throwable> void nonNull(GenericTarget[] objectArr, Supplier<? extends GenericException> exceptionSupplier) throws GenericException {
        if (Arrays.stream(objectArr).filter(Objects::nonNull).count() != objectArr.length) {
            throw exceptionSupplier.get();
        }
    }

    /**
     * 非null和空断言
     *
     * @param collection          集合
     * @param <GenericCollection> 通用集合
     * @return 集合
     */
    private static <GenericCollection extends Collection<?>> GenericCollection nonEmpty(GenericCollection collection) {
        if (Objects.isNull(collection) || collection.isEmpty()) {
            throw new NullPointerException(DEFAULT_COLLECTION_NOT_EMPTY_EXCEPTION_MSG);
        }
        return collection;
    }


    //-- Stream --//

    /**
     * Description: 集合不为 null 并且 nonEmpty 的断言
     *
     * @param collection          泛型集合
     * @param exceptionSupplier   异常 {@link Supplier}
     * @param <GenericCollection> 通用集合
     * @param <GenericException>  通用异常
     * @return GenericCollection 集合
     * @throws GenericException 不满足断言时
     */
    public static <GenericCollection extends Collection<?>, GenericException extends Throwable> GenericCollection nonEmpty(GenericCollection collection, Supplier<? extends GenericException> exceptionSupplier) throws GenericException {
        if (exceptionSupplier == null) {
            nonEmpty(collection);
        } else if (Objects.isNull(collection) || collection.isEmpty()) {
            throw exceptionSupplier.get();
        }
        return collection;
    }

    /**
     * Description: 唯一 断言
     *
     * @param collection                    泛型集合
     * @param exceptionSupplierWhenEmpty    集合为空时抛出的异常 {@link Supplier}
     * @param exceptionSupplierWhenMultiple 集合大小不唯 1 时抛出的异常 {@link Supplier}
     * @param <GenericCollection>           通用集合
     * @param <GenericException>            通用异常
     * @return GenericCollection 集合
     * @throws GenericException 不满足断言时
     * @see Assert#nonEmpty(Collection)
     */
    public static <GenericCollection extends Collection<?>, GenericException extends Throwable> GenericCollection single(GenericCollection collection,
                                                                                                                         Supplier<? extends GenericException> exceptionSupplierWhenEmpty,
                                                                                                                         Supplier<? extends GenericException> exceptionSupplierWhenMultiple
    ) throws GenericException {
        GenericCollection _collection = nonEmpty(collection, exceptionSupplierWhenEmpty);
        if (_collection.size() > 1) {
            throw exceptionSupplierWhenMultiple.get();
        }
        return _collection;
    }

    /**
     * 唯一 断言
     *
     * @param stream             {@link Stream}
     * @param exceptionSupplier  异常 {@link Supplier}
     * @param <GenericException> 通用异常
     * @return Stream
     * @throws GenericException 不满足断言时
     */
    public static <GenericException extends Throwable> Stream<?> single(Stream<?> stream, Supplier<? extends GenericException> exceptionSupplier) throws GenericException {
        final Object[] arr = nonNull(stream).toArray();
        if (arr.length != 1) {
            throw exceptionSupplier.get();
        }
        return Stream.of(arr);
    }
}
