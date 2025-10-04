package com.haithem.taskmanagemnt.mapper;

import com.haithem.taskmanagemnt.dto.TaskRequest;
import com.haithem.taskmanagemnt.dto.TaskResponse;
import com.haithem.taskmanagemnt.model.Task;
import org.mapstruct.*;

/**
 * MapStruct mapper for converting between Task entity and DTOs
 * This provides compile-time safe mapping without reflection overhead
 */
@Mapper(
    componentModel = "spring",
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface TaskMapper {

    /**
     * Convert Task entity to TaskResponse DTO
     * Maps all fields including audit fields
     */
    TaskResponse toResponse(Task task);

    /**
     * Convert TaskRequest DTO to Task entity
     * Ignores id and audit fields as they are auto-generated
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    Task toEntity(TaskRequest request);

    /**
     * Update existing Task entity from TaskRequest DTO
     * Only updates fields that are not null in the request
     * Preserves id and audit fields
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromRequest(TaskRequest request, @MappingTarget Task task);
}
