package dat3.remoteapiexampleopenai2.repository;

import dat3.remoteapiexampleopenai2.entity.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QuestionRepo extends JpaRepository<Question, Integer> {
}
