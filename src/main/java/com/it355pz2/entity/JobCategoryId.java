package com.it355pz2.entity;

import java.io.Serializable;
import java.util.Objects;

public class JobCategoryId implements Serializable {
    private final Long jobId;
    private final Long categoryId;

    public JobCategoryId(Long jobId, Long categoryId) {
        this.jobId = jobId;
        this.categoryId = categoryId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(jobId, categoryId);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof JobCategoryId that)) return false;
        return Objects.equals(this.jobId, that.jobId) && Objects.equals(this.categoryId, that.categoryId);
    }
}
