package project.api;

@RestController
@RequestMapping("/projects")
public class ProjectController {

    private final ProjectService service;

    ProjectController(ProjectService projectService) {
        this.service = projectService;
    }

    @PostMapping()
    public ResponseEntity<Project> create(){}

    @GetMapping("/{projectId}")
    public ResponseEntity<Project> findById(@PathVariable("projectId") UUID projectId) {
        Optional<Project> project = projectService.findById(projectId);
        return project.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping()
    public ResponseEntity<List<Project>> findAll() {
        List<Project> projects = projectService.findAll();
        return ResponseEntity.ok(projects);
    }

    @PutMapping("/{projectId}")
    public ResponseEntity<Project> update(@PathVariable("projectId") UUID projectId, @RequestBody Project project) {
        Optional<Project> updated = projectService.update(projectId, project);
        return updated.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{projectId}")
    public ResponseEntity<Void> delete(@PathVariable("projectId") UUID projectId) {
        boolean deleted = projectService.delete(projectId);
        if (deleted) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
}
}