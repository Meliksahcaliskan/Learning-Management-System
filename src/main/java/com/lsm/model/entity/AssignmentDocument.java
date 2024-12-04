package com.lsm.model.entity;

import com.lsm.model.entity.base.AppUser;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "assignment_documents")
public class AssignmentDocument {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "assignment_doc_seq")
    @SequenceGenerator(name = "assignment_doc_seq", sequenceName = "assignment_docs_seq", allocationSize = 1)
    private Long id;

    @Column(name = "file_name", nullable = false)
    private String fileName;

    @Column(name = "file_path", nullable = false)
    private String filePath;

    @Column(name = "upload_time", nullable = false)
    private LocalDateTime uploadTime;

    @Column(name = "file_type")
    private String fileType;

    @Column(name = "file_size")
    private Long fileSize;

    @OneToOne
    @JoinColumn(name = "assignment_id", nullable = false)
    private Assignment assignment;

    @ManyToOne
    @JoinColumn(name = "uploaded_by", nullable = false)
    private AppUser uploadedBy;

    @Column(name = "is_teacher_upload")
    private boolean isTeacherUpload;
}