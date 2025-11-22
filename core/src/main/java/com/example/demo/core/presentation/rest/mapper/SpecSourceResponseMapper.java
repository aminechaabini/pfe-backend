package com.example.demo.core.presentation.rest.mapper;

import com.example.demo.core.presentation.rest.dto.response.spec.SpecSourceDetailResponse;
import com.example.demo.core.presentation.rest.dto.response.spec.SpecSourceResponse;
import com.example.demo.core.domain.spec.SpecSource;
import org.mapstruct.Mapper;

import java.util.List;

/**
 * MapStruct mapper: SpecSource (domain) → SpecSourceResponse (API DTO).
 */
@Mapper(componentModel = "spring", uses = {EndpointResponseMapper.class})
public interface SpecSourceResponseMapper {

    /**
     * Map to summary response.
     */
    default SpecSourceResponse toResponse(SpecSource domain) {
        if (domain == null) {
            return null;
        }

        return new SpecSourceResponse(
                domain.getId(),
                domain.getName(),
                domain.getFileName(),
                domain.getSpecType(),
                domain.getVersion(),
                domain.getEndpointCount(),
                domain.getCreatedAt(),
                domain.getUpdatedAt()
        );
    }

    /**
     * Map to detail response (includes endpoints).
     */
    default SpecSourceDetailResponse toDetailResponse(SpecSource domain) {
        if (domain == null) {
            return null;
        }

        EndpointResponseMapper endpointMapper = new EndpointResponseMapperImpl();

        return new SpecSourceDetailResponse(
                domain.getId(),
                domain.getName(),
                domain.getFileName(),
                domain.getSpecType(),
                domain.getVersion(),
                domain.getEndpointCount(),
                endpointMapper.toResponseList(domain.getEndpoints()),
                domain.getCreatedAt(),
                domain.getUpdatedAt()
        );
    }

    List<SpecSourceResponse> toResponseList(List<SpecSource> domains);
}
