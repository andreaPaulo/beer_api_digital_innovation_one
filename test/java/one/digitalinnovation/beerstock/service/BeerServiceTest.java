package one.digitalinnovation.beerstock.service;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.lessThan;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import one.digitalinnovation.beerstock.builder.BeerDTOBuilder;
import one.digitalinnovation.beerstock.dto.BeerDTO;
import one.digitalinnovation.beerstock.entity.Beer;
import one.digitalinnovation.beerstock.exception.BeerAlreadyRegisteredException;
import one.digitalinnovation.beerstock.exception.BeerNotFoundException;
import one.digitalinnovation.beerstock.mapper.BeerMapper;
import one.digitalinnovation.beerstock.repository.BeerRepository;

@ExtendWith(MockitoExtension.class)
public class BeerServiceTest {

	private static final long INVALID_BEER_ID = 1L;

	@Mock
	private BeerRepository beerRepository;

	private BeerMapper beerMapper = BeerMapper.INSTANCE;

	@InjectMocks
	private BeerService beerService;

	// criando uma Beer
	@Test
	void whenBeerInformedThenItShouldBeCreated() throws BeerAlreadyRegisteredException {
		// givem
		BeerDTO beerDTO = BeerDTOBuilder.builder().build().toBeerDTO();
		Beer expectedSaveBeer = beerMapper.toModel(beerDTO);

		// when
		when(beerRepository.findByName(beerDTO.getName())).thenReturn(Optional.empty());
		when(beerRepository.save(expectedSaveBeer)).thenReturn(expectedSaveBeer);

		// then

		BeerDTO createBeerDTO = beerService.createBeer(beerDTO);

		assertThat(createBeerDTO.getId(), Matchers.is(equalTo(beerDTO.getId())));
		assertThat(createBeerDTO.getName(), Matchers.is(equalTo(beerDTO.getName())));
		assertThat(createBeerDTO.getQuantity(), Matchers.is(equalTo(beerDTO.getQuantity())));

	}

	// já existe essa beer lanca exception
	@Test
	void whenalreadyRegisteredBeerInformadthenExceptionShouldBeThrown() {
		// givem
		BeerDTO beerDTO = BeerDTOBuilder.builder().build().toBeerDTO();
		Beer duplicatedBeer = beerMapper.toModel(beerDTO);

		// when
		when(beerRepository.findByName(beerDTO.getName())).thenReturn(Optional.of(duplicatedBeer));

		// then
		assertThrows(BeerAlreadyRegisteredException.class, () -> beerService.createBeer(beerDTO));

	}
	// Pesquisa por nome

	@Test
	void whenValidBeerNameisGivenThanReturnABeer() throws BeerNotFoundException {
		// given
		BeerDTO expectedFoundBeerDTO = BeerDTOBuilder.builder().build().toBeerDTO();
		Beer expectedFoundBeer = beerMapper.toModel(expectedFoundBeerDTO);

		// when
		when(beerRepository.findByName(expectedFoundBeer.getName())).thenReturn(Optional.of(expectedFoundBeer));

		// then
		BeerDTO foundBeerDTO = beerService.findByName(expectedFoundBeerDTO.getName());

		assertThat(foundBeerDTO, is(equalTo(expectedFoundBeerDTO)));

	}
	// nome vazio retorna exception

	@Test
	void whenNotRegisteredBeerNameisGivenThanthrowsException() {
		// given
		BeerDTO expectedFoundBeerDTO = BeerDTOBuilder.builder().build().toBeerDTO();

		// when
		when(beerRepository.findByName(expectedFoundBeerDTO.getName())).thenReturn(Optional.empty());

		// then
		assertThrows(BeerNotFoundException.class, () -> beerService.findByName(expectedFoundBeerDTO.getName()));

	}

	// restorna lista
	@Test
	void whenListBeerIsCallledThenReturnAsListOfBeers() {
		// given
		BeerDTO expectedFoundBeerDTO = BeerDTOBuilder.builder().build().toBeerDTO();
		Beer expectedFoundBeer = beerMapper.toModel(expectedFoundBeerDTO);
		// when
		when(beerRepository.findAll()).thenReturn(Collections.singletonList(expectedFoundBeer));

		// then
		List<BeerDTO> listAllBeersDTO = beerService.listAll();
		assertThat(listAllBeersDTO, is(not(empty())));
		assertThat(listAllBeersDTO.get(0), is(equalTo(expectedFoundBeerDTO)));

	}

	// Lista vazia
	@Test
	void whenListBeerIsCallledThenReturnAnEmptyListOfBeers() {

		// when
		when(beerRepository.findAll()).thenReturn(Collections.EMPTY_LIST);

		// then
		List<BeerDTO> listAllBeersDTO = beerService.listAll();
		assertThat(listAllBeersDTO, is(empty()));

	}
	// deleted uma beer
	@Test
	void whenExclusionIsCalledWithValidIdThenABeerShouldBeDeleted() throws BeerNotFoundException {
		// given
		BeerDTO expectedDeletedBeerDTO = BeerDTOBuilder.builder().build().toBeerDTO();
		Beer expectedDeletedBeer = beerMapper.toModel(expectedDeletedBeerDTO);

		// when
		when(beerRepository.findById(expectedDeletedBeerDTO.getId())).thenReturn(Optional.of(expectedDeletedBeer));
		doNothing().when(beerRepository).deleteById(expectedDeletedBeerDTO.getId());

		// then
		beerService.deleteById(expectedDeletedBeerDTO.getId());

		verify(beerRepository, times(1)).findById(expectedDeletedBeerDTO.getId());
		verify(beerRepository, times(1)).deleteById(expectedDeletedBeerDTO.getId());
	}

}
