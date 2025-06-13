package asp.repository;

import asp.model.StorageUnit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StorageUnitRepository extends JpaRepository<StorageUnit,Integer> {
    Optional<StorageUnit> findByName(String name);
    Optional<StorageUnit> findByParentIsNull();
}

