package com.moat.pipeline;

public interface PipelineStep {

    void execute(PipelineContext context);
}
