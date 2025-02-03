package march.repositories;

import march.entities.GitHub;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GitHubRepository extends JpaRepository<GitHub,Integer> {
    boolean existsByNameApi(String name);
    List<GitHub> findGitHubByActiveProd(boolean active, Sort sort);

}
