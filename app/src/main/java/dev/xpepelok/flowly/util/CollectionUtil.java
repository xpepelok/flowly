package dev.xpepelok.flowly.util;

import dev.xpepelok.bank.transaction.model.Transaction;
import lombok.experimental.UtilityClass;

import java.util.*;

@UtilityClass
public final class CollectionUtil {
    public static <T> List<T> asSafeList(Collection<T> in) {
        return in == null ? List.of() : List.copyOf(in);
    }

    public static List<Transaction> dedupeConcat(
            List<Transaction> firstList,
            List<Transaction> secondList
    ) {
        if ((firstList == null || firstList.isEmpty()) && (secondList == null || secondList.isEmpty())) return List.of();
        
        var set = new LinkedHashSet<String>();
        var out = new ArrayList<Transaction>();

        for (var t : concat(firstList, secondList)) {
            String key = t.getSender() + "|" + t.getRecipient() + "|" + t.getSum() + "|" + t.getTransactionDate();
            if (set.add(key)) out.add(t);
        }
        
        return out;
    }

    public static <T> Iterable<T> concat(List<T> first, List<T> second) {
        return () -> new Iterator<>() {
            final Iterator<T> firstIterator = (first == null ? List.<T>of() : first).iterator();
            final Iterator<T> secondIterator = (second == null ? List.<T>of() : second).iterator();

            @Override 
            public boolean hasNext() { 
                return firstIterator.hasNext() || secondIterator.hasNext(); 
            }
            
            @Override public T next() { 
                return firstIterator.hasNext() ? firstIterator.next() : secondIterator.next(); 
            }
        };
    }
}
