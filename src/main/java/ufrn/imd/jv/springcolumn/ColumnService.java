package ufrn.imd.jv.springcolumn;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

@Service
public class ColumnService {

    @Value("${service.user}")
    private String userService;

    @Value("${service.board}")
    private String boardService;

    private final ColumnRepository repository;

    private final RestTemplate restTemplate;

    @Autowired
    public ColumnService(ColumnRepository repository, RestTemplate restTemplate) {
        this.repository = repository;
        this.restTemplate = restTemplate;
    }

    public boolean entidadeEhValida(String path, Long id) {
        ResponseEntity<String> response = restTemplate.exchange(
                path+"/"+id,
                HttpMethod.GET,
                null,
                String.class);
        return response.getStatusCode().is2xxSuccessful();
    }

    public ColumnEntity save(ColumnEntity columnEntity) {
        if(columnEntity == null) {
            throw new RuntimeException("Entidade não informada");
        }

        if (columnEntity.getUserId() == null) {
            throw new RuntimeException("Usuário não informado");
        }
        if(!entidadeEhValida(userService, columnEntity.getUserId())) {
            throw new RuntimeException("Usuário informado não existe");
        }

        if (columnEntity.getBoardId() == null) {
            throw new RuntimeException("Board não informado");
        }
        if(!entidadeEhValida(boardService, columnEntity.getBoardId())) {
            throw new RuntimeException("Board informado não existe");
        }

        if (columnEntity.getName() == null) {
            throw new RuntimeException("Nome da coluna não informada");
        }
        if(!columnEntity.getName().trim().equals("")) {
            throw new RuntimeException("Nome da coluna informado é inválido");
        }
        Optional<ColumnEntity> optValue = repository.findByNameAndBoardId(columnEntity.getName(), columnEntity.getBoardId());
        if(optValue.isPresent()) {
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
