package ufrn.imd.jv.springcolumn;

import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class ColumnResilience {
    private final UserServiceInterface userService;
    private final BoardServiceInterface boardService;
    private final ColumnRepository repository;

    @Autowired
    public ColumnResilience(UserServiceInterface userService,
                            BoardServiceInterface boardService,
                            ColumnRepository repository) {
        this.userService = userService;
        this.boardService = boardService;
        this.repository = repository;
    }

    @CircuitBreaker(name = "isUserValid_cb", fallbackMethod = "isUserKnown")
    @Bulkhead(name = "isUserValid_bh", fallbackMethod = "isUserKnown", type = Bulkhead.Type.THREADPOOL)
    public boolean isUserValid(Long id) {
        ResponseEntity<Map<String, String>> response = userService.getUser(id);
        return response.getStatusCode().is2xxSuccessful();
    }

    @CircuitBreaker(name = "isBoardValid_cb", fallbackMethod = "isBoardKnown")
    @Bulkhead(name = "isBoardValid_bh", fallbackMethod = "isBoardKnown", type = Bulkhead.Type.THREADPOOL)
    public boolean isBoardValid(Long id) {
        ResponseEntity<Map<String, String>> response = boardService.getBoard(id);
        return response.getStatusCode().is2xxSuccessful();
    }

    public boolean isUserKnown(Long id, Throwable t) {
        System.err.println(
                "Não foi possível consultar o service de usuários devido a: " +
                        t.getMessage() +
                        "Consultando boards locais em busca do usuário"
        );
        if (repository.existsByUserId(id)) {
            System.err.println("Usuário foi encontrado, portanto é válido");
            return true;
        } else {
            System.err.println("Não foi encontrado board criado pelo usuário de id=" + id);
            return false;
        }
    }

    public boolean isBoardKnown(Long id, Throwable t) {
        System.err.println(
                "Não foi possível consultar o service de boards devido a: " +
                        t.getMessage() +
                        "Consultando columns locais em busca do board"
        );
        if (repository.existsByBoardId(id)) {
            System.err.println("Board foi encontrado, portanto é válido");
            return true;
        } else {
            System.err.println("Não foi encontrado column associado ao board de id=" + id);
            return false;
        }
    }
}
