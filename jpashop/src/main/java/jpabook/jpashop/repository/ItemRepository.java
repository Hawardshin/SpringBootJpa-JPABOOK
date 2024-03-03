package jpabook.jpashop.repository;

import java.util.List;

import org.springframework.stereotype.Repository;

import jakarta.persistence.EntityManager;
import jpabook.jpashop.domain.item.Item;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class ItemRepository {
	private final EntityManager em;

	public void save(Item item) {
		if (item.getId() == null) { //make new object
			em.persist(item);
		} else { //already register in db
			em.merge(item); //merge is similar to update
		}
	}

	public Item findOne(Long id) {
		return em.find(Item.class, id);
	}

	public List<Item> findAll(){
		return em.createQuery("select i from Item i", Item.class).getResultList();
	}

}
