package ufrn.imd.jv.springcolumn;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Map;

@FeignClient("SPRINGBOARD")
public interface BoardServiceInterface {
    @RequestMapping(method = RequestMethod.GET, value = "/boards/{id}")
    ResponseEntity<Map<String, String>> getBoard(@PathVariable Long id);
}
