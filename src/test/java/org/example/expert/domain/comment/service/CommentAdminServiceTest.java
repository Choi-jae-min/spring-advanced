package org.example.expert.domain.comment.service;

import org.example.expert.domain.comment.repository.CommentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CommentAdminServiceTest {

    @Mock
    private CommentRepository commentRepository;
    @InjectMocks
    private CommentAdminService commentAdminService;

    @Test
    public void comment를_정상적으로_삭제한다() {
        // given
        long commentId = 1L;

        given(commentRepository.existsById(commentId)).willReturn(true);

        // when
        commentAdminService.deleteComment(commentId);

        // then
        verify(commentRepository, times(1)).deleteById(commentId);
    }

    @Test
    public void 존재하지_않는_comment_삭제시_에러발생() {
        // given
        long commentId = 1L;

        given(commentRepository.existsById(commentId)).willReturn(false);

        // when
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            commentAdminService.deleteComment(commentId);
        });

        // then
        assertEquals("댓글이 존재하지 않습니다.", exception.getMessage());
        verify(commentRepository, never()).deleteById(anyLong());
    }
}