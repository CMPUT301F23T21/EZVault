package com.example.ezvault;

import com.google.android.gms.tasks.Task;

import java.util.function.Supplier;

/**
 * An object that interacts interacts with the database to
 * create, read, update or delete objects.
 * @param <T> The type of object to create, read, update or delete.
 * @param <ID> The type that represents a unique identifier for the object.
 */
public abstract class AbstractDAO<T, ID> {
    /**
     * The underlying connection with firebase.
     */
    protected FirebaseBundle firebase;

    /**
     * Constructs an AbstractDAO
     * @param firebase The firebase bundle that this DAO uses
     */
    public AbstractDAO(FirebaseBundle firebase) {
        this.firebase = firebase;
    }

    /**
     * Adds an object of type {@code T} into the database.
     * @param t The object to be created on the database.
     * @return The ID of the newly created database entry.
     */
    abstract Task<ID> create(T t);

    /**
     * Reads an object from the database based on a unique identifier.
     * @param id The unique identifier of the object.
     * @return The object from the database.
     */
    abstract Task<T> read(ID id);

    /**
     * Updates the value of an existing object on the database.
     * @param id The id of the object.
     * @param t The new value to update the object to.
     * @return A {@code Task} representing the asynchronous operation.
     */
    abstract Task<Void> update(ID id, T t);

    /**
     * Deletes an object from the database.
     * @param id The id of the object to be deleted.
     * @return A {@code Task} representing the asynchronous operation.
     */
    abstract Task<Void> delete(ID id);

    /**
     * The deferred version of {@code create}
     * @param t The object to write to the database.
     * @return A thunk representing the creation operation.
     */
    public Supplier<Task<ID>> deferCreate(T t) {
        return () -> create(t);
    }

    /**
     * The deferred version of {@code read}
     * @param id The unique identifier of the object.
     * @return A thunk representing the reading operation.
     */
    public final Supplier<Task<T>> deferRead(ID id) {
        return () -> read(id);
    }

    /**
     * The deferred version of {@code update}
     * @param id The unique identifier of the object.
     * @param t The new value for the object to be set to.
     * @return A thunk representing the updating operation.
     */
    public final Supplier<Task<Void>> deferUpdate(ID id, T t) {
        return () -> update(id, t);
    }

    /**
     * The deferred version of {@code delete}
     * @param id The unique identifier of the object.
     * @return A thunk representing the deletion operation.
     */
    public final Supplier<Task<Void>> deferDelete(ID id) {
        return () -> delete(id);
    }
}
