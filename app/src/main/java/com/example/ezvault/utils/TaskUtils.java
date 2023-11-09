package com.example.ezvault.utils;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class TaskUtils {
    public static <S> Task<Void> drop(Task<S> task) {
        return onSuccessProc(task, x -> {});
    }

    public static <S> Task<S> onFailureTask(Task<?> t, Supplier<Task<S>> thunk, Exception e)  {
        return t.continueWithTask(task -> {
            if (!t.isSuccessful()) {
                return thunk.get();
            } else {
                return Tasks.forException(e);
            }
        });
    }

    public static <S, T> Task<T> onSuccess(Task<S> task, Function<S, T> f) {
        return task.onSuccessTask(s -> Tasks.forResult(f.apply(s)));
    }

    public static <S> Task<S> onTrueFalseTask(Task<Boolean> task, Supplier<Task<S>> trueThunk, Supplier<Task<S>> falseThunk) {
        return task.onSuccessTask(b -> {
            if (b) {
                return trueThunk.get();
            } else {
                return falseThunk.get();
            }
        });
    }

    public static <S> Task<Void> onSuccessProc(Task<S> task, Consumer<S> f) {
        return onSuccess(task, s -> {
            f.accept(s);
            return null;
        });
    }
}
