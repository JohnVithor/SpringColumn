package ufrn.imd.jv.springcolumn;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping(path = "columns")
public class ColumnController {

    private final ColumnService service;

    @Autowired
    public ColumnController(ColumnService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<ColumnEntity> createColumn(@RequestBody ColumnEntity columnEntity) {
        return ResponseEntity.ok(service.save(columnEntity));
    }

    @GetMapping
    public ResponseEntity<Page<ColumnEntity>> getPage(
            @RequestParam(name = "pg", required = false) Optional<Integer> page,
            @RequestParam(name = "lim", required = false) Optional<Integer> limit) {
        return service.getPage(page.orElse(0), limit.orElse(10));
    }

    @GetMapping(path = "board/{id}")
    public ResponseEntity<Page<ColumnEntity>> getByBoardId(@PathVariable Long id,
                                                           @RequestParam(name = "pg", required = false) Optional<Integer> page,
                                                           @RequestParam(name = "lim", required = false) Optional<Integer> limit) {
        return service.getByBoardId(id, page.orElse(0), limit.orElse(10));
    }

    @GetMapping(path = "{id}")
    public ResponseEntity<ColumnEntity> getById(@PathVariable Long id) {
        return service.getById(id);
    }
}
