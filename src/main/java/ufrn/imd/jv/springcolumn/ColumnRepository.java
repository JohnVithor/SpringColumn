package ufrn.imd.jv.springcolumn;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ColumnRepository extends JpaRepository<ColumnEntity, Long> {

    Page<ColumnEntity> findByBoardIdIs(Long id, PageRequest pageRequest);

    Optional<ColumnEntity> findByNameAndBoardId(String name, Long id);

    boolean existsByUserId(Long id);

    boolean existsByBoardId(Long id);

}
