package com.example.bulimovaTask.specification;

import com.example.bulimovaTask.dto.TaskFilterDTO;
import com.example.bulimovaTask.entity.Task;
import com.example.bulimovaTask.entity.TaskStatus;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

public class TaskSpecification {

    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    public static Specification<Task> build(
            TaskFilterDTO filter,
            Long userId
    ) {
        return (root, query, cb) -> {

            List<Predicate> predicates = new ArrayList<>();

            if (filter.getStatus() != null) {
                try {
                    TaskStatus status = TaskStatus.valueOf(
                            filter.getStatus().trim()
                    );
                    predicates.add(cb.equal(root.get("status"), status));


                    if (status == TaskStatus.NEW) {
                        predicates.add(cb.isNull(root.get("deadline")));
                    }

                } catch (IllegalArgumentException ex) {
                    return cb.disjunction();
                }
            }


            if (filter.getTitle() != null) {
                predicates.add(
                        cb.equal(root.get("title"), filter.getTitle())
                );
            }


            if (userId != null) {
                predicates.add(
                        cb.equal(root.get("user").get("id"), userId)
                );
            }


            try {
                LocalDateTime from = parse(filter.getDeadlineFrom());
                LocalDateTime to = parse(filter.getDeadlineTo());

                if (from != null) {
                    predicates.add(
                            cb.greaterThanOrEqualTo(
                                    root.get("deadline"), from
                            )
                    );
                }
                if (to != null) {
                    predicates.add(
                            cb.lessThanOrEqualTo(
                                    root.get("deadline"), to
                            )
                    );
                }
            } catch (DateTimeParseException ex) {
                return cb.disjunction();
            }

            try {
                LocalDateTime from = parse(filter.getCreatedFrom());
                LocalDateTime to = parse(filter.getCreatedTo());

                if (from != null) {
                    predicates.add(
                            cb.greaterThanOrEqualTo(
                                    root.get("createDate"), from
                            )
                    );
                }
                if (to != null) {
                    predicates.add(
                            cb.lessThanOrEqualTo(
                                    root.get("createDate"), to
                            )
                    );
                }
            } catch (DateTimeParseException ex) {
                return cb.disjunction();
            }


            try {
                LocalDateTime from = parse(filter.getUpdatedFrom());
                LocalDateTime to = parse(filter.getUpdatedTo());

                if (from != null) {
                    predicates.add(
                            cb.greaterThanOrEqualTo(
                                    root.get("updateDate"), from
                            )
                    );
                }
                if (to != null) {
                    predicates.add(
                            cb.lessThanOrEqualTo(
                                    root.get("updateDate"), to
                            )
                    );
                }
            } catch (DateTimeParseException ex) {
                return cb.disjunction();
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    private static LocalDateTime parse(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return LocalDateTime.parse(value.trim(), FORMATTER);
    }
}
