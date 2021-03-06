package com.ssafy.api.application.service.community;

import com.ssafy.api.application.dto.request.community.CommunityModifyPatchDTO;
import com.ssafy.api.application.dto.request.community.CommunityRegisterPostDTO;
import com.ssafy.api.application.dto.response.community.CommunityGetListResponseDTO;
import com.ssafy.api.application.dto.response.community.CommunityGetListWrapperResponseDTO;
import com.ssafy.api.application.dto.response.community.CommunityResponseDTO;
import com.ssafy.api.domain.dao.community.Comment;
import com.ssafy.api.domain.dao.community.Community;
import com.ssafy.api.domain.enums.CommunityCode;
import com.ssafy.api.domain.repository.comment.CommentRepository;
import com.ssafy.api.domain.repository.community.CommunityRepository;
import com.ssafy.api.domain.repository.mountain.MountainRepository;
import com.ssafy.api.domain.repository.user.UserRepository;
import com.ssafy.api.exception.CommunityNotFoundException;
import com.ssafy.api.exception.MountainNotFoundException;
import com.ssafy.api.exception.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CommunityServiceImpl implements CommunityService {

    private final CommunityRepository communityRepository;

    private final MountainRepository mountainRepository;

    private final CommentRepository commentRepository;

    private final UserRepository userRepository;

    @Override
    public void registerCommunity(CommunityRegisterPostDTO communityRegisterPostDTO, String email) {

        Community community = Community.builder()
                .communityCode(communityRegisterPostDTO.getCommunityCode())
                .title(communityRegisterPostDTO.getTitle())
                .description(communityRegisterPostDTO.getDescription())
                .createTime(LocalDateTime.now())
                .user(userRepository.findByEmailAndDelete(email, false)
                        .orElseThrow(() -> new UserNotFoundException("?????? ????????? ???????????? ????????????.")))
                .mountain(mountainRepository.findById(communityRegisterPostDTO.getMountainId())
                        .orElseThrow(() -> new MountainNotFoundException("??? ????????? ???????????? ????????????.")))
                .build();

        communityRepository.save(community);

    }

    @Override
    public CommunityGetListWrapperResponseDTO getCommunityList(CommunityCode communityCode, int page) {

        List<Community> communityList = communityRepository.findByCommunityCodeAndDeleteOrderByCreateTimeDesc(communityCode, false, PageRequest.of(page - 1, 6))
                .orElseThrow(() -> new CommunityNotFoundException("??? ????????? ???????????? ????????????."));

        return convertCommunityListToDTO(communityList);

    }

    @Override
    public CommunityGetListWrapperResponseDTO searchCommunity(CommunityCode communityCode, String type, String keyword, int page) {

        List<Community> communityList = new ArrayList<>();

        switch (type) {
            case "td":
                communityList = communityRepository.findByCommunityCodeAndDeleteAndTitleContainingOrDescriptionContainingOrderByCreateTimeDesc(
                                communityCode, false, keyword, keyword, PageRequest.of(page - 1, 6)
                        )
                        .orElseThrow(() -> new CommunityNotFoundException("??? ????????? ???????????? ????????????."));

                break;
            case "t":
                communityList = communityRepository.findByCommunityCodeAndDeleteAndTitleContainingOrderByCreateTimeDesc(
                                communityCode, false, keyword, PageRequest.of(page - 1, 6)
                        )
                        .orElseThrow(() -> new CommunityNotFoundException("??? ????????? ???????????? ????????????."));

                break;
            case "d":
                communityList = communityRepository.findByCommunityCodeAndDeleteAndDescriptionContainingOrderByCreateTimeDesc(
                                communityCode, false, keyword, PageRequest.of(page - 1, 6)
                        )
                        .orElseThrow(() -> new CommunityNotFoundException("??? ????????? ???????????? ????????????."));

                break;
            case "n":
                communityList = communityRepository.findByCommunityCodeAndDeleteAndUserOrderByCreateTimeDesc(
                                communityCode, false,
                                userRepository.findByNicknameAndDelete(keyword, false)
                                        .orElseThrow(() -> new UserNotFoundException("?????? ????????? ???????????? ????????????.")),
                                PageRequest.of(page - 1, 6)
                        )
                        .orElseThrow(() -> new CommunityNotFoundException("??? ????????? ???????????? ????????????."));

                break;
        }

        return convertCommunityListToDTO(communityList);

    }

    @Override
    public CommunityResponseDTO getCommunity(String communityId) {

        Community community = communityRepository.findByIdAndDelete(communityId, false)
                .orElseThrow(() -> new CommunityNotFoundException("??? ????????? ???????????? ????????????."));

        community.increaseViews();

        communityRepository.save(community);

        return CommunityResponseDTO.builder()
                .nickname(community.getUser().getNickname())
                .title(community.getTitle())
                .description(community.getDescription())
                .views(community.getViews())
                .createTime(community.getCreateTime())
                .build();

    }

    @Override
    public void modifyCommunity(CommunityModifyPatchDTO communityModifyPatchDTO) {

        Community community = communityRepository.findByIdAndDelete(communityModifyPatchDTO.getCommunityId(), false)
                .orElseThrow(() -> new CommunityNotFoundException("??? ????????? ???????????? ????????????."));

        community.changeCommunity(communityModifyPatchDTO.getTitle(), communityModifyPatchDTO.getDescription());

        communityRepository.save(community);

    }

    @Override
    public void deleteCommunity(String communityId) {

        Community community = communityRepository.findById(communityId)
                .orElseThrow(() -> new CommunityNotFoundException("??? ????????? ???????????? ????????????."));

        community.changeDelete();

        communityRepository.save(community);

        List<Comment> commentList = commentRepository.findByCommunityAndDeleteOrderByCreateTime(community, false)
                .orElse(null);

        commentList.forEach(comment -> {
            comment.changeDelete();

            commentRepository.save(comment);
        });

    }

    private CommunityGetListWrapperResponseDTO convertCommunityListToDTO(List<Community> communityList) {

        List<CommunityGetListResponseDTO> communityGetListResponseDTOList = new ArrayList<>();

        communityList.forEach(community ->
                communityGetListResponseDTOList.add(
                        CommunityGetListResponseDTO.builder()
                                .communityId(community.getId())
                                .nickname(community.getUser().getNickname())
                                .title(community.getTitle())
                                .createTime(community.getCreateTime())
                                .comments(commentRepository.countByCommunityAndDelete(community, false))
                                .build()
                )
        );

        return CommunityGetListWrapperResponseDTO.builder()
                .communityGetListResponseDTOList(communityGetListResponseDTOList)
                .build();

    }

}
