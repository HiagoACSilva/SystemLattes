package br.ufma.ppgs.controller;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import br.ufma.ppgs.service.ImportadorService;

@RestController
@RequestMapping("/api/importador")
public class ImportadorControler {
    @Autowired
	ImportadorService imp;
		
    @GetMapping("/docente")
    public ResponseEntity initDocente() {
        try { 
            //TODO: mudar forma de passar repositório para web...
		    List<String> refsDocente = imp.importadorEmMassaDocente("C:\\Users\\Hiago\\Downloads\\BasicWebScraping-master\\data\\xmls\\docentes\\");    
            return ResponseEntity.ok(refsDocente);
        }
        catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
	}

    @GetMapping("/discente")
    public ResponseEntity initDiscente() {
        try {
            List<String> refsDiscente = imp.importadorEmMassaDiscente("C:\\Users\\Hiago\\Downloads\\BasicWebScraping-master\\data\\xmls\\discentes\\");
            return ResponseEntity.ok(refsDiscente);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
