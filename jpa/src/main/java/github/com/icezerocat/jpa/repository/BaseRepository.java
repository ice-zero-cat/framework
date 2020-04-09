package github.com.icezerocat.jpa.repository;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.lang.NonNull;

import java.io.Serializable;
import java.util.List;

/**
 * ProjectName: [framework]
 * Package:     [github.com.icezerocat.admin.jpa.service.BaseRepository]
 * Description: Repository封装
 * CreateDate:  2020/4/3 18:56
 *
 * @author 0.0.0
 * @version 1.0
 */
@SuppressWarnings("unused")
@NoRepositoryBean
public interface BaseRepository<T, ID extends Serializable> extends JpaRepository<T, ID>, JpaSpecificationExecutor<T> {
    /**
     * 按ID列表和指定的排序查找所有对象。
     *
     * @param ids  对象的ID列表不能为空
     * @param sort 指定的排序不得为null
     * @return 对象列表
     */
    @NonNull
    List<T> findAllByIdIn(@NonNull Iterable<ID> ids, @NonNull Sort sort);

    /**
     * 按ID列表删除。
     *
     * @param ids 对象的ID列表不能为空
     * @return 受影响的行数
     */
    long deleteByIdIn(@NonNull Iterable<ID> ids);
}
