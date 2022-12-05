package ufrn.imd.jv.springcolumn;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

@Service
public class ColumnService {

    private final UserServiceInterface userService;

    private final BoardServiceInterface boardService;

    private final ColumnRepository repository;

    @Autowired
    public ColumnService(UserServiceInterface userServiceInterface,
                         BoardServiceInterface boardServiceInterface,
                         ColumnRepository repository) {
        this.userService = userServiceInterface;
        this.boardService = boardServiceInterface;
        this.repository = repository;
    }

    public ColumnEntity save(ColumnEntity columnEntity) {
        if (columnEntity == null) {
            throw new RuntimeException("Entidade não informada");
        }
        if (columnEntity.getUserId() == null) {
            throw new RuntimeException("Usuário não informado");
        }
        ResponseEntity<Map<String, String>> responseUser = userService.getUser(columnEntity.getUserId());
        if (!responseUser.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException("Usuário informado não existe");
        }
        if (columnEntity.getBoardId() == null) {
            throw new RuntimeException("Board não informado");
        }
        ResponseEntity<Map<String, String>> responseBoard = boardService.getBoard(columnEntity.getBoardId());
        if (!responseBoard.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException("Board informado não existe");
        }
        if (columnEntity.getName() == null) {
            throw new RuntimeException("Nome da coluna não informada");
        }
        if (!columnEntity.getName().trim().equals("")) {
            throw new RuntimeException("Nome da coluna informado é inválido");
        }
        Optional<ColumnEntity> optValue = repository.findByNameAndBoardId(columnEntity.getName(), columnEntity.getBoardId());
        if (optValue.isPresent()) {
            throw new RuntimeException("Nome da coluna já está em uso nesse board");
        }
        return repository.save(columnEntity);
    }

    public ResponseEntity<Page<ColumnEntity>> getPage(int page, int limit) {
        return ResponseEntity.ok(repository.findAll(PageRequest.of(page, limit)));
    }

    public ResponseEntity<Page<ColumnEntity>> getByBoardId(Long id, int page, int limit) {
        return ResponseEntity.ok(repository.findByBoardIdIs(id, PageRequest.of(page, limit)));
    }

    public ResponseEntity<ColumnEntity> getById(Long id) {
        return repository.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(404).build());
    }
}
