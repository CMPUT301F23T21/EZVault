/*
Provides an AbstractDAO class that is intended to be extended by subclasses.
The subclasses should specialize in accessing the database for a specific type of object.
 */

package com.example.ezvault.data.database;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;

import java.util.ArrayList;

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
    public abstract Task<ID> create(T t);

    /**
     * Reads an object from the database based on a unique identifier.
     * @param id The unique identifier of the object.
     * @return The object from the database.
     */
    public abstract Task<T> read(ID id);

    /**
     * Updates the value of an existing object on the database.
     * @param id The id of the object.
     * @param t The new value to update the object to.
     * @return A {@code Task} representing the asynchronous operation.
     */
    public abstract Task<Void> update(ID id, T t);

    /**
     * Deletes an object from the database.
     * @param id The id of the object to be deleted.
     * @return A {@code Task} representing the asynchronous operation.
     */
    public abstract Task<Void> delete(ID id);

    /**
     * Performs a batch read operation for multiple objects from the database based on their unique identifiers.
     * @param ids The list of unique identifiers for the objects to be read.
     * @return A {@code Task} containing an {@code ArrayList} of objects of type {@code T} corresponding to the provided identifiers.
     */
    public Task<ArrayList<T>> pluralRead(ArrayList<ID> ids) {
        Task<ArrayList<T>> task = Tasks.forResult(new ArrayList<>());
        if (ids.size() == 0) {
            return task;
        }
        for (ID id : ids) {
            task = task.onSuccessTask(ts -> {
                Task<T> tTask = read(id);
                return tTask.continueWith(t -> {
                    ts.add(tTask.getResult());
                    return ts;
                });
            });
        }
        return task;
    }
}
