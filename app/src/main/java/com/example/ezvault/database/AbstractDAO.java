package com.example.ezvault.database;

import com.example.ezvault.FirebaseBundle;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Transaction;

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
     * Represents a creation of an object in a transaction.
     * @param transaction The current transaction state
     * @param t The object to create
     * @return The ID of the object created
     */
    abstract ID createTransactional(Transaction transaction, T t);

    /**
     * Represents reading an object in a transaction
     * @param transaction The current transaction state
     * @param id The id of the object to read
     * @return The object read
     */
    abstract T readTransactional(Transaction transaction, ID id);

    /**
     * Update an object in a transaction
     * @param transaction The current transaction state
     * @param t The new value of the object
     * @param id The id of the object to be updated
     */
    abstract void updateTransactional(Transaction transaction, T t, ID id);

    /**
     * Delete an object in a transaction
     * @param transaction The current transaction state
     * @param id The id of the object to delete
     */
    abstract void deleteTransactional(Transaction transaction, ID id);
}
