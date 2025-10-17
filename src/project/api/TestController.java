package project.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import project.app.RunService;
import project.app.TestCatalogService;
import project.domain.Run;
import project.domain.TestCase;
import project.infra.InMemoryProjectRepository;

import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/projects/{projectId}/suites/{suiteId}/tests")
public class TestController {
    private final TestCatalogService catalogService;
    private final RunService runService;

    public TestController() {
        var repo = new InMemoryProjectRepository();
        this.catalogService = new TestCatalogService(repo);
        this.runService = new RunService();
    }

    @PostMapping
    public ResponseEntity<TestCase> add(@PathVariable("projectId") UUID projectId,
                                        @PathVariable("suiteId") UUID suiteId,
                                        @RequestBody TestCase test) {
        Optional<TestCase> created = catalogService.addTest(projectId, suiteId, test);
        return created.map(c -> ResponseEntity.created(java.net.URI.create("/projects/" + projectId + "/suites/" + suiteId + "/tests/" + c.getId())).body(c))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/{testId}")
    public ResponseEntity<TestCase> get(@PathVariable("projectId") UUID projectId,
                                        @PathVariable("suiteId") UUID suiteId,
                                        @PathVariable("testId") UUID testId) {
        Optional<TestCase> t = catalogService.get(projectId, suiteId, testId);
        return t.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/{testId}/validate")
    public ResponseEntity<String> validate(@PathVariable("projectId") UUID projectId,
                                           @PathVariable("suiteId") UUID suiteId,
                                           @PathVariable("testId") UUID testId) {
        Optional<TestCase> t = catalogService.get(projectId, suiteId, testId);
        if (t.isEmpty()) return ResponseEntity.notFound().build();
        // simplistic validation: requestTemplate present
        boolean ok = t.get().getRequestTemplate() != null && !t.get().getRequestTemplate().isBlank();
        if (ok) return ResponseEntity.ok("validated");
        return ResponseEntity.badRequest().body("invalid test definition");
    }

    @PostMapping("/{testId}/run")
    public ResponseEntity<Run> run(@PathVariable("projectId") UUID projectId,
                                   @PathVariable("suiteId") UUID suiteId,
                                   @PathVariable("testId") UUID testId,
                                   @RequestParam(value = "env", required = false, defaultValue = "dev") String env) {
        Run run = runService.startTestRun(projectId, suiteId, testId, env);
        return ResponseEntity.ok(run);
    }
}
