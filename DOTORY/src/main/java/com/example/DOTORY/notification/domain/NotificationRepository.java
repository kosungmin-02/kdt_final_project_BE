package com.example.DOTORY.notification.domain;

import com.example.DOTORY.notification.application.port.NotificationPort;
import com.google.api.core.ApiFuture;
import com.google.api.core.ApiFutureCallback;
import com.google.api.core.ApiFutures;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.WriteResult;
import com.google.common.util.concurrent.MoreExecutors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Slf4j
@Repository
@RequiredArgsConstructor
public class NotificationRepository implements NotificationPort {

    private final Firestore db;

    @Override
    public List<Notification> findAllByUserId(String userId) {
        try {
            ApiFuture<QuerySnapshot> future = db.collection("users").document(userId).collection("notifications").get();
            QuerySnapshot snapshot = future.get(); // 비동기 작업이 끝날 때까지 대기
            if (snapshot.isEmpty()) {
                return List.of();
            }

            List<Notification> notifications = new ArrayList<>();
            for (QueryDocumentSnapshot document : snapshot.getDocuments()) {
                Notification notification = document.toObject(Notification.class);
                notification.setId(document.getId()); // Firestore 문서 ID를 객체의 id 필드에 설정
                notifications.add(notification);
            }
            return notifications;

        } catch (InterruptedException | ExecutionException e) {
            log.error("Firestore에서 알림 조회 중 오류 발생: userId={}", userId, e);
            Thread.currentThread().interrupt();
            return List.of();
        }
    }

    @Override
    public void save(Notification notification, String userId) {
        ApiFuture<DocumentReference> future = db.collection("users").document(userId).collection("notifications").add(notification);
        ApiFutures.addCallback(future, new ApiFutureCallback<DocumentReference>() {
            @Override
            public void onFailure(Throwable t) {
                log.error("Firestore에 알림 저장 실패: userId={}", userId, t);
            }

            @Override
            public void onSuccess(DocumentReference result) {
                log.info("Firestore에 알림 저장 성공: userId={}, path={}", userId, result.getPath());
            }
        }, MoreExecutors.directExecutor());
    }
}