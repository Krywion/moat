package com.moat.pipeline;

import org.springframework.stereotype.Component;

/**
 * Krok 4 — wzbogacenie danymi rynkowymi. Na razie no-op; pola rynkowe
 * pozostają null. Yahoo dojdzie w Fazie 4.
 */
@Component
public class EnrichmentStep implements PipelineStep {

    @Override
    public void execute(PipelineContext context) {
        // celowo puste — krok 4 implementowany w Fazie 4
    }
}
