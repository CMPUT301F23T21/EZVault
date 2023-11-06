package com.example.ezvault.utils;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;

import java.util.function.Consumer;
import java.util.function.Function;

public class TaskUtils {
    public static <S, T> Task<T> onSuccess(Task<S> task, Function<S, T> f) {
        return task.onSuccessTask(s -> Tasks.forResult(f.apply(s)));
    }

    public static <S> Task<Void> onSuccessProc(Task<S> task, Consumer<S> f) {
        return onSuccess(task, s -> {
            f.accept(s);
            return null;
        });
    }
}
