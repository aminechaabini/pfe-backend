# API Testing Orchestrator - Documentation

This folder contains all project documentation organized by architectural layer.

## Folder Structure

```
documentation/
├── 1-domain/           Domain layer documentation
├── 2-persistence/      Persistence layer documentation
├── 3-service-layer/    Service & mapper layer documentation
└── 4-general/          General project documentation
```

## Documentation by Category

### 1. Domain Layer (`1-domain/`)
- **DOMAIN_FIXES_SUMMARY.md** - Summary of domain model improvements and fixes

### 2. Persistence Layer (`2-persistence/`)
- **PERSISTENCE_LAYER_ARCHITECTURE.md** - Complete architecture overview
- **PERSISTENCE_DESIGN_DECISIONS.md** - Key design decisions and rationale
- **AGREED_PERSISTENCE_DESIGN.md** - Final agreed-upon design
- **PERSISTENCE_IMPLEMENTATION_SUMMARY.md** - Implementation summary
- **PERSISTENCE_QUICK_REFERENCE.md** - Quick reference guide
- **PERSISTENCE_ERD.md** - Entity Relationship Diagram
- **H2_SETUP_COMPLETE.md** - H2 database setup guide

### 3. Service Layer (`3-service-layer/`)
- **SERVICE_LAYER_ARCHITECTURE_PLAN.md** - Complete service layer design
- **SERVICE_LAYER_SUMMARY.md** - Executive summary
- **SERVICE_LAYER_IMPLEMENTATION_CHECKLIST.md** - Implementation roadmap
- **MAPSTRUCT_IMPLEMENTATION_GUIDE.md** - MapStruct mapper guide
- **MAPPER_IMPLEMENTATION_SUMMARY.md** - Mapper implementation summary (COMPLETED)

### 4. General (`4-general/`)
- **HELP.md** - General project help and getting started

## Reading Order for New Developers

1. Start with `4-general/HELP.md` for project overview
2. Read `1-domain/DOMAIN_FIXES_SUMMARY.md` to understand domain models
3. Read `2-persistence/PERSISTENCE_LAYER_ARCHITECTURE.md` for database design
4. Read `3-service-layer/SERVICE_LAYER_ARCHITECTURE_PLAN.md` for service design
5. Refer to specific guides as needed

## Implementation Status

| Layer | Status | Documentation |
|-------|--------|---------------|
| Domain | ✅ Complete | 1 document |
| Persistence | ✅ Complete | 7 documents |
| Mappers | ✅ Complete | 2 documents |
| Services | ⏳ Planned | 3 documents |
| Infrastructure | ⏳ Partial | - |

## Quick Links

### For Architects
- Persistence Design: `2-persistence/PERSISTENCE_LAYER_ARCHITECTURE.md`
- Service Design: `3-service-layer/SERVICE_LAYER_ARCHITECTURE_PLAN.md`

### For Developers
- Mapper Implementation: `3-service-layer/MAPPER_IMPLEMENTATION_SUMMARY.md`
- Quick Reference: `2-persistence/PERSISTENCE_QUICK_REFERENCE.md`
- Implementation Checklist: `3-service-layer/SERVICE_LAYER_IMPLEMENTATION_CHECKLIST.md`

### For Database Admins
- ERD: `2-persistence/PERSISTENCE_ERD.md`
- H2 Setup: `2-persistence/H2_SETUP_COMPLETE.md`

---

**Last Updated:** 2025-11-10
**Project:** AI-Powered API Testing Orchestrator
