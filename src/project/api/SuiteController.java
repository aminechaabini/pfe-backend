package project.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import project.app.RunService;
import project.app.SuiteService;
import project.domain.Run;
import project.domain.TestSuite;
import project.infra.InMemoryProjectRepository;

import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/projects/{projectId}/suites")
public class SuiteController {
    private final SuiteService suiteService;
    private final RunService runService;

    public SuiteController() {
        var repo = new InMemoryProjectRepository();
        this.suiteService = new SuiteService(repo);
        this.runService = new RunService();
    }

    @PostMapping
    public ResponseEntity<TestSuite> create(@PathVariable("projectId") UUID projectId, @RequestBody TestSuite suite) {
        Optional<TestSuite> created = suiteService.create(projectId, suite);
        return created.map(c -> ResponseEntity.created(java.net.URI.create("/projects/" + projectId + "/suites/" + c.getId())).body(c))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/{suiteId}")
    public ResponseEntity<TestSuite> get(@PathVariable("projectId") UUID projectId, @PathVariable("suiteId") UUID suiteId) {
        Optional<TestSuite> s = suiteService.get(projectId, suiteId);
        return s.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/{suiteId}/validate")
    public ResponseEntity<String> validate(@PathVariable("projectId") UUID projectId, @PathVariable("suiteId") UUID suiteId) {
        Optional<TestSuite> s = suiteService.get(projectId, suiteId);
        if (s.isEmpty()) return ResponseEntity.notFound().build();
        // simplistic validation
        boolean ok = !s.get().getTests().isEmpty();
        if (ok) return ResponseEntity.ok("validated");
        return ResponseEntity.badRequest().body("no tests in suite");
    }

    @PostMapping("/{suiteId}/run")
    public ResponseEntity<Run> run(@PathVariable("projectId") UUID projectId,
                                   @PathVariable("suiteId") UUID suiteId,
                                   @RequestParam(value = "env", required = false, defaultValue = "dev") String env,
                                   @RequestParam(value = "maxConcurrency", required = false, defaultValue = "1") int maxConcurrency) {
        Run run = runService.startRun(projectId, suiteId, env, maxConcurrency);
        return ResponseEntity.ok(run);
    }
}
