package dev.dotspace.common.storage;

import dev.dotspace.common.SpaceObjects;
import dev.dotspace.common.storage.container.Storage;
import dev.dotspace.common.storage.container.StorageData;
import dev.dotspace.common.storage.container.StorageType;
import dev.dotspace.common.storage.method.StorageMethod;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.*;
import java.util.stream.Stream;


public final class StorageManager<STORAGE extends Storage> {
  private final @NotNull Class<STORAGE> storageClass;
  private @NotNull Instance<STORAGE>[] instances;
  private boolean active;

  public StorageManager(@NotNull final Class<STORAGE> storageClass) {
    this.storageClass = SpaceObjects.throwIfNull(storageClass);
    this.instances = (Instance<STORAGE>[]) new Instance[0];
    this.active = true;

  }

  /**
   * @param storage
   * @return
   */
  public synchronized boolean implementWrapper(@Nullable final STORAGE storage) {
    this.checkIfDeactivated();

    if (!this.storageClass.isInstance(storage)) {  //Return with false if given storage is null.
      //  this.logger.error("Given wrapper is not instance of {}.", this.wrapperClass);
      return false;
    }

    final StorageData storageData = storage.getClass().getAnnotation(StorageData.class);

    if (storageData == null) {
      //  this.logger.error("Given wrapper has no @DataWrapperInfo annotation is null.");
      return false;
    }

    InstanceMethod[] instanceMethods = new InstanceMethod[0];
    for (final Method method : storage.getClass().getMethods()) { //Goto every available method of storage class
      final StorageMethod storageMethod = method.getAnnotation(StorageMethod.class);

      if (storageMethod == null) { //Goto next method if annotation is missing.
        continue;
      }

      final InstanceMethod instanceMethod = new InstanceMethod(
        method.getGenericReturnType(),
        method.getGenericParameterTypes(),
        method.getName(),
        method);

      instanceMethods = Arrays.copyOf(instanceMethods, instanceMethods.length + 1);
      instanceMethods[instanceMethods.length - 1] = instanceMethod;
      //this.logger.info("{}", wrapperInstanceMethod);
    }

    if (this.instances.length > 0) {
      final Set<InstanceMethod> methodSet = Set.of(instanceMethods);
      for (final Instance<STORAGE> instance : this.instances) {
        final Set<InstanceMethod> instanceMethodSet = Set.of(instance.instanceMethods);

        if (instanceMethodSet.containsAll(methodSet) && methodSet.containsAll(instanceMethodSet)) {
          continue;
        }

        this.deactivate();
        return false;
      }
    }

    this.instances = Arrays.copyOf(this.instances, this.instances.length + 1);
    final Instance<STORAGE> storageInstance = new Instance<>(
      storage,
      storageData.name(),
      storageData.storageType(),
      storageData.priority(),
      instanceMethods);

    this.instances[this.instances.length - 1] = storageInstance;

    //  this.logger.info("Successfully added wrapper[{}] to manager methods=[{}].", wrapperInfo.name(), methods.stream().map(WrapperInstanceMethod::name).collect(Collectors.joining(", ")));
    return true;
  }

  private @Nullable Optional<Instance<STORAGE>> typeInstance(@Nullable final StorageType storageType) {
    return this.filteredAndOrdered(SpaceObjects.throwIfNull(storageType)).findFirst();
  }

  public @Nullable STORAGE type(@Nullable final StorageType storageType) {
    return null;
  }


  private @NotNull Stream<Instance<STORAGE>> filtered(@NotNull final StorageType storageType) {
    return Arrays
      .stream(this.instances)
      .filter(wrapperWrapperR -> wrapperWrapperR.storageType() == storageType);
  }

  private @NotNull Stream<Instance<STORAGE>> filteredAndOrdered(@NotNull final StorageType storageType) {
    return this
      .filtered(storageType)
      .sorted((base, compare) -> Byte.compare(compare.priority(), base.priority()) /*Inverted to set highest as first.*/);
  }

  private void deactivate() {
    this.active = false;
    this.instances = null;
    System.out.println("Deactivated class of an error.");
  }

  private void checkIfDeactivated() {
    if (!this.active) {
      throw new RuntimeException("Manager already deactivated.");
    }
  }

  //Methods and classes for
  private record Instance<STORAGE extends Storage>(@NotNull STORAGE storage,
                                                   @NotNull String name,
                                                   @NotNull StorageType storageType,
                                                   byte priority,
                                                   @NotNull InstanceMethod[] instanceMethods) {
  }

  private record InstanceMethod(@NotNull Type returnType,
                                @NotNull Type[] arguments,
                                @NotNull String name,
                                @NotNull Method method) {

    @Override
    public boolean equals(@Nullable final Object object) {
      if (this == object) {
        return true;
      }

      if (object == null || getClass() != object.getClass()) {
        return false;
      }

      InstanceMethod instanceMethod = (InstanceMethod) object;

      return this.returnType.equals(instanceMethod.returnType()) &&
        Arrays.equals(arguments, instanceMethod.arguments()) &&
        name.equals(instanceMethod.name());
    }

    @Override
    public int hashCode() {
      int result = Objects.hash(returnType, name);
      result = 31 * result + Arrays.hashCode(arguments);
      return result;
    }
  }
}
