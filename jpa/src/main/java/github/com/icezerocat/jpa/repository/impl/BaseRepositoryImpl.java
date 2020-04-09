package github.com.icezerocat.jpa.repository.impl;

import github.com.icezerocat.jpa.repository.BaseRepository;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * ProjectName: [framework]
 * Package:     [github.com.icezerocat.admin.jpa.service.impl.BaseRepositoryImpl]
 * Description: BaseRepository 实现类
 * CreateDate:  2020/4/3 19:02
 *
 * @author 0.0.0
 * @version 1.0
 */
@SuppressWarnings("unused")
public class BaseRepositoryImpl<T, ID extends Serializable> extends SimpleJpaRepository<T, ID> implements BaseRepository<T, ID> {
    private final JpaEntityInformation<T, ID> entityInformation;

    /**
     * 构造
     *
     * @param entityInformation 实体信息
     * @param entityManager     实体管理
     */
    public BaseRepositoryImpl(JpaEntityInformation<T, ID> entityInformation, EntityManager entityManager) {
        super(entityInformation, entityManager);
        this.entityInformation = entityInformation;
    }


    @NonNull
    @Override
    public List<T> findAllByIdIn(@NonNull Iterable<ID> ids, @NonNull Sort sort) {
        Assert.notNull(ids, "The given Iterable of Id's must not be null!");

        //为空返回空对象
        if (!ids.iterator().hasNext()) {
            return Collections.emptyList();
        }

        /*
          判断是否有复合ID
         */
        if (!this.entityInformation.hasCompositeId()) {
            ByIdsSpecification<T> specification = new ByIdsSpecification<>(this.entityInformation);
            TypedQuery<T> query = super.getQuery(specification, sort);
            return query.setParameter(specification.parameter, ids).getResultList();
        } else {
            List<T> results = new ArrayList<>();
            ids.forEach(id -> super.findById(id).ifPresent(results::add));
            return results;
        }
    }

    @Override
    public long deleteByIdIn(@NonNull Iterable<ID> ids) {
        List<T> domains = findAllById(ids);
        deleteInBatch(domains);
        return domains.size();
    }

    /**
     * JPA重写Specification的toPredicate多条件查询
     *
     * @param <T> 实体类
     */
    private static final class ByIdsSpecification<T> implements Specification<T> {
        private static final long serialVersionUID = 1L;
        private final JpaEntityInformation<T, ?> entityInformation;
        @Nullable
        ParameterExpression<Iterable> parameter;

        ByIdsSpecification(JpaEntityInformation<T, ?> entityInformation) {
            this.entityInformation = entityInformation;
        }

        /**
         * 多条件查询
         *
         * @param root  Criteria查询的根对象
         * @param query 查询条件，包含着查询的各个部分，比如：select 、from、where、group by、order by
         * @param cb    用来构建CritiaQuery的构建器对象
         * @return 查询条件或者是条件组合（Predicate：一个简单或复杂的谓词类型）
         */
        @Override
        public Predicate toPredicate(Root<T> root, @NonNull CriteriaQuery<?> query, CriteriaBuilder cb) {
            Path<?> path = root.get(this.entityInformation.getIdAttribute());
            this.parameter = cb.parameter(Iterable.class);
            return path.in(this.parameter);
        }
    }
}
