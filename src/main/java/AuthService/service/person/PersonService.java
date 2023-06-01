package AuthService.service.person;

import AuthService.dto.person.PersonDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient("person-service")
public interface PersonService {
    @PostMapping
    PersonDTO createPerson(@RequestBody PersonDTO personDTO);

    @GetMapping("/{username}")
    PersonDTO getPersonDTOByUsername(@PathVariable(name = "username") String username);
}
