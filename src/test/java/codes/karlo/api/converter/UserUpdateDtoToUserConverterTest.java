package codes.karlo.api.converter;

import codes.karlo.api.dto.UserUpdateDto;
import codes.karlo.api.exception.UserDoesntExistException;
import codes.karlo.api.model.User;
import codes.karlo.api.repository.UserRepository;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatCode;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserUpdateDtoToUserConverterTest {

    private UserUpdateDtoToUserConverter converter;

    @Mock
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        this.converter = new UserUpdateDtoToUserConverter(this.userRepository);
    }

    @Test
    void shouldConvert() {
        final UserUpdateDto userUpdateDto = UserUpdateDto.builder().id(1L).build();
        final User user = User.builder().id(1L).build();

        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user));

        assertThat(converter.convert(userUpdateDto)).isEqualTo(user);
    }

    @Test
    void shouldFailConvert() {
        final UserUpdateDto userUpdateDto = UserUpdateDto.builder().id(1L).build();

        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThatCode(() -> converter.convert(userUpdateDto))
                .isInstanceOf(UserDoesntExistException.class)
                .hasMessage(String.format("User with ID %d has not been found", userUpdateDto.getId()));
    }
}