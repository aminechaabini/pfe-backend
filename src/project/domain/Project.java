package project.domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity 
@Table(name = "projects")
public class Project {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(name="name", nullable=false, length=255, unique=true)
    private String name;

    @Column(name="description", nullable=true, length=255, unique=false)
    private String description;

    @Column(name="createdAt", nullable=false, length=255, unique=false)
    private Timestamp createdAt;

}
