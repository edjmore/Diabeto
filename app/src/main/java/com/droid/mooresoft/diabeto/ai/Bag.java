package com.droid.mooresoft.diabeto.ai;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;

/**
 * Created by Edward Moore on 4/27/16.
 * Copyright (c) 2016 MooreSoft. All rights reserved.
 * <p/>
 * A Bag is very similar to a Set, but in addition it tracks the frequency of each item added.
 * The frequency of any item in the Bag can be retrieved with the getCount(1) method.
 */
public class Bag<T> extends HashSet<T> {

   // A mapping from objects in the Bag to their frequencies.
   private HashMap<T, Integer> mFrequencyMap;

   /**
    * Creates a new Bag data structure.
    */
   public Bag() {
      mFrequencyMap = new HashMap<>();
   }

   /**
    * Counts the number of instances of the given object in the Bag.
    *
    * @param object An object.
    * @return The number of instances of the object in the Bag.
    */
   public int getCount(T object) {
      if (!contains(object)) return 0;
      return mFrequencyMap.get(object);
   }

   /**
    * Adds an instance of the given object to the Bag.
    *
    * @param object The instance to add.
    * @return True iff the added object is the first of its kind in the Bag.
    */
   @Override
   public boolean add(T object) {
      int frequency = contains(object) ? mFrequencyMap.get(object) : 0;
      mFrequencyMap.put(object, ++frequency);
      return super.add(object);
   }

   /**
    * Adds the Collection of instances to the Bag.
    *
    * @param collection A Collection of instances to add.
    * @return True iff at least one of the objects is the first of its kind in the Bag.
    */
   @Override
   public boolean addAll(Collection<? extends T> collection) {
      boolean wasModified = false;
      for (T object : collection) {
         wasModified = add(object) ? true : wasModified;
      }
      return true;
   }

   /**
    * Removes an instance of the given object from the Bag.
    *
    * @param object The instance to remove.
    * @return True iff there is at least one more instance of the given object in the Bag.
    */
   @Override
   public boolean remove(Object object) {
      int frequency = 0;
      if (contains(object)) {
         T typedObject = (T) object;
         frequency = mFrequencyMap.get(typedObject);
         mFrequencyMap.put(typedObject, --frequency);
      }
      boolean hasRemaining = true;
      if (0 == frequency) {
         super.remove(object);
         hasRemaining = false;
      }
      return hasRemaining;
   }

   /**
    * Removes an instance of each of the given objects from the Bag.
    *
    * @param collection A Collection of instances to remove.
    * @return False iff the Bag contains no remaining instances of any of the objects from the Collection.
    */
   @Override
   public boolean removeAll(Collection<?> collection) {
      boolean hasRemaining = false;
      for (Object object : collection) {
         hasRemaining = remove(object) ? true : hasRemaining;
      }
      return hasRemaining;
   }
}
