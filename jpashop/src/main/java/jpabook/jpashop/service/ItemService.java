package jpabook.jpashop.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jpabook.jpashop.domain.item.Book;
import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.repository.ItemRepository;
import lombok.RequiredArgsConstructor;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ItemService {
	private final ItemRepository itemRepository;

	@Transactional
	public void saveItem(Item item){
		itemRepository.save(item);
	}

	@Transactional
	public Item updateItem(Long itemId, Book bookParam){
		Item findItem = itemRepository.findOne(itemId); //영속상태인 엔티티를 가져옴
		findItem.setPrice(bookParam.getPrice());
		findItem.setName(bookParam.getName());
		findItem.setStockQuantity(bookParam.getStockQuantity());
		//여기서 save를 호출하거나 persist를 호출 할 필요가 없다!! (영속성 컨텍스트를 가져왔기 때문에)
		//이게 끝나면 Spring의 Transactional에 의해셔 commit이 일어납니다.
		//즉 jpa는  flush를 날립니다. (변경감지)
		return findItem;
	}

	public List<Item> findItems(){
		return itemRepository.findAll();
	}

	public Item findOne(Long itemId){
		return itemRepository.findOne(itemId);
	}
}
