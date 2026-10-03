package lv.gderuki.sagapatternsim.model;

/**
 * Represents a step in a saga.
 *
 * @param <T> the type of the context
 */
public interface SagaStep<T> {

    /**
     * Processes the given context.
     *
     * @param context the context to process
     * @return true if the processing was successful, false otherwise
     */
    boolean process(T context);

    /**
     * Rolls back the processing of the given context.
     *
     * @param context the context to rollback
     */
    void rollback(T context);

}
